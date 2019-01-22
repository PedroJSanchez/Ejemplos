/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import GUI.AgenteDemoJFrame;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author pedroj
 */
public class AgenteDemo extends Agent {
    private AgenteDemoJFrame myGui;
    private int ejecuciones;
    
    /**
     * Se ejecuta cuando se inicia el agente
     */
    @Override
    protected void setup() {
        //Configuración del GUI y presentación
        myGui = new AgenteDemoJFrame(this);
        myGui.setVisible(true);
        myGui.presentarSalida("Se inicia la ejecución de " + this.getName() + "\n");
        
        //Incialización de variables
        ejecuciones = 0;
        
        //Registro de la Ontología
        
        //Registro en Página Amarillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
	ServiceDescription sd = new ServiceDescription();
	sd.setType("Tipo de Servicio");
	sd.setName("Nombre del Servicio");
	dfd.addServices(sd);
	try {
            DFService.register(this, dfd);
	}
	catch (FIPAException fe) {
            fe.printStackTrace();
	}
        
        // Se añaden las tareas principales
        addBehaviour(new TareaEjemplo(this, 10000));
    }
    
    /**
     * Se ejecuta al finalizar el agente
     */
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
        myGui.dispose();
        System.out.println("Finaliza la ejecución de " + this.getName());
    }
    
    //Métodos del agente
    
    
    //Clases que representan las tareas del agente
    public class TareaEjemplo extends TickerBehaviour {
        //Tarea de ejemplo que se repite cada 10 segundos
        public TareaEjemplo(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
           ejecuciones++;
           myGui.presentarSalida("\nEjecución número: " + ejecuciones);
        }
        
    }
    
}
