/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.Agent;

/**
 *
 * @author pedroj
 * Esqueleto de agente para la estructura general que deben tener todos los
 * agentes
 */
public class AgentePlantilla extends Agent {
    //Variables del agente

    @Override
    protected void setup() {
       //Inicialización de las variables del agente
       
       //Configuración del GUI
       
       //Registro del agente en las Páginas Amarrillas
       
       //Registro de la Ontología
       
       System.out.println("Se inicia la ejecución del agente: " + this.getName());
       //Añadir las tareas principales
    }

    @Override
    protected void takeDown() {
       //Eliminar registro del agente en las Páginas Amarillas
       
       //Liberación de recursos, incluido el GUI
       
       //Despedida
       System.out.println("Finaliza la ejecución del agente: " + this.getName());
    }
    
    //Métodos de trabajo del agente
    
    
    //Clases internas que representan las tareas del agente
      
}
