/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import GUI.FormularioJFrame;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import util.Punto2D;

/**
 *
 * @author pedroj
 */
public class AgenteFormulario extends Agent {
    private FormularioJFrame myGui;
    private AID[] agentesConsola;
    private AID[] agentesOperacion;
    private ArrayList<String> mensajesPendientes;

    @Override
    protected void setup() {
        //Inicialización de variables
        mensajesPendientes = new ArrayList();
        
        //Configuración del GUI
        myGui = new FormularioJFrame(this);
        myGui.setVisible(true);

        //Registro de la Ontología
        
        
        //Añadir tareas principales
        addBehaviour(new TareaBuscarAgentes(this, 5000));
        addBehaviour(new TareaEnvioConsola(this,10000));
    }

    @Override
    protected void takeDown() {
        //Se liberan los recuros y se despide
        myGui.dispose();
        System.out.println("Finaliza la ejecución de " + this.getName());
    }
    
    public void enviarPunto2D(Punto2D punto) {
        addBehaviour(new TareaEnvioOperacion(punto));
    }
    
    public class TareaBuscarAgentes extends TickerBehaviour {
        //Se buscarán agentes consola y operación
        public TareaBuscarAgentes(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            DFAgentDescription template;
            ServiceDescription sd;
            DFAgentDescription[] result;
            
            //Busca agentes consola
            template = new DFAgentDescription();
            sd = new ServiceDescription();
            sd.setName("Consola");
            template.addServices(sd);
            
            try {
                result = DFService.search(myAgent, template); 
                if (result.length > 0) {
                    agentesConsola = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        agentesConsola[i] = result[i].getName();
                    }
                }
                else {
                    //No se han encontrado agentes consola
                    agentesConsola = null;
                }
            }
            catch (FIPAException fe) {
		fe.printStackTrace();
            }
            
            //Busca agentes operación
            template = new DFAgentDescription();
            sd = new ServiceDescription();
            sd.setName("Operacion");
            template.addServices(sd);
            
            try {
                result = DFService.search(myAgent, template); 
                if (result.length > 0) {
                    agentesOperacion = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        agentesOperacion[i] = result[i].getName();
                    }
                    myGui.activarEnviar(true);
                }
                else {
                    //No se han encontrado agentes operación
                    agentesOperacion = null;
                    myGui.activarEnviar(false);
                } 
            }
            catch (FIPAException fe) {
		fe.printStackTrace();
            }
        }
    }
    
    public class TareaEnvioOperacion extends OneShotBehaviour {
        private Punto2D punto;

        public TareaEnvioOperacion(Punto2D punto) {
            this.punto = punto;
        }
        

        @Override
        public void action() {
            //Se envía la operación a todos los agentes operación
            ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
            mensaje.setSender(myAgent.getAID());
            //Se añaden todos los agentes operación
            for (int i=0; i < agentesOperacion.length; i++) {
                mensaje.addReceiver(agentesOperacion[i]);
            }
            mensaje.setContent(punto.getX() + "," + punto.getY());
            
            send(mensaje);
            
            //Se añade el mensaje para la consola
            mensajesPendientes.add("Enviado a: " + agentesOperacion.length +
                    " agentes el punto: " + mensaje.getContent());
        }
    }
    
    public class TareaEnvioConsola extends TickerBehaviour {

        public TareaEnvioConsola(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            ACLMessage mensaje;
            if (agentesConsola != null) {
                if (!mensajesPendientes.isEmpty()) {
                    mensaje = new ACLMessage(ACLMessage.INFORM);
                    mensaje.setSender(myAgent.getAID());
                    mensaje.addReceiver(agentesConsola[0]);
                    mensaje.setContent(mensajesPendientes.remove(0));
            
                    myAgent.send(mensaje);
                }
                else {
                    //Si queremos hacer algo si no tenemos mensajes
                    //pendientes para enviar a la consola
                }
            }
        }
    }
}
