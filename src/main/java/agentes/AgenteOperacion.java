/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Random;
import util.Punto2D;

/**
 *
 * @author pedroj
 */
public class AgenteOperacion extends Agent {
    private AID[] agentesConsola;
    private ArrayList<String> mensajesPendientes;
    private ArrayList<Punto2D> operacionesPendientes;
    private Random rnd;

    @Override
    protected void setup() {
        //Inicialización de variables
        mensajesPendientes = new ArrayList();
        operacionesPendientes = new ArrayList();
        rnd = new Random();
        
        //Registro en páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
	ServiceDescription sd = new ServiceDescription();
	sd.setType("Utilidad");
	sd.setName("Operacion");
	dfd.addServices(sd);
	try {
            DFService.register(this, dfd);
	}
	catch (FIPAException fe) {
            fe.printStackTrace();
	}
        
        //Regisro de la Ontología
        
        //Añadir las tareas principales
        addBehaviour(new TareaBuscarConsola(this,5000));
        addBehaviour(new TareaRecepcionOperacion());
        addBehaviour(new TareaEnvioConsola(this,10000));
    }
    
    @Override
    protected void takeDown() {
        //Desregistro de las Páginas Amarillas
        try {
            DFService.deregister(this);
	}
            catch (FIPAException fe) {
            fe.printStackTrace();
	}
        
        //Se liberan los recuros y se despide
        
        System.out.println("Finaliza la ejecución de " + this.getName());
    }
    
    private String operacion (Punto2D punto) {
        double resultado;
        
        //Se realiza una operación elegida de forma aleatoria
        int i = rnd.nextInt(4);
        
        switch (i) {
            case 0:
                //Suma
                resultado = punto.getX() + punto.getY();
                return "Se ha realizado la suma de " + punto
                        + "\ncon el resultado: "+ resultado;
            case 1:
                //Resta
                resultado = punto.getX() - punto.getY();
                return "Se ha realizado la resta de " + punto
                        + "\ncon el resultado: "+ resultado;
            default:
                //Multiplicación
                resultado = punto.getX() * punto.getY();
                return "Se ha realizado la multiplicación de " + punto
                        + "\ncon el resultado: "+ resultado;
        }
        
    } 
    
    public class TareaBuscarConsola extends TickerBehaviour {
        //Se buscarán consolas 
        public TareaBuscarConsola(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            //Busca agentes consola
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setName("Consola");
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template); 
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
        }
    }
    
    public class TareaRecepcionOperacion extends CyclicBehaviour {

        @Override
        public void action() {
            //Recepción de la información para realizar la operación
            MessageTemplate plantilla = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage mensaje = myAgent.receive(plantilla);
            if (mensaje != null) {
                //procesamos el mensaje
                String[] contenido = mensaje.getContent().split(",");
                
                Punto2D punto = new Punto2D();
                punto.setX(Double.parseDouble(contenido[0]));
                punto.setY(Double.parseDouble(contenido[1]));
                
                operacionesPendientes.add(punto);
                
                addBehaviour(new TareaRealizarOperacion());
            } 
            else
                block();
            
        }
    }
    
    public class TareaRealizarOperacion extends OneShotBehaviour {

        @Override
        public void action() {
            //Realizar una operacion pendiente y añadir el mensaje
            //para la consola
            
            Punto2D punto = operacionesPendientes.remove(0);
            
            mensajesPendientes.add(operacion(punto));
        }
        
    }

    public class TareaEnvioConsola extends TickerBehaviour {
        //Tarea de ejemplo que se repite cada 10 segundos
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
                    //Acciones que queremos hacer si no tenemos
                    //mensajes pendientes
                }
            }
        } 
    }
}
