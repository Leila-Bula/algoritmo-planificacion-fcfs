import proceso.Dato;
import proceso.Datos;

import java.util.HashMap;
import java.util.Set;

class Main {
    public static void main(String[] args) {
        long time = Math.round(Math.random()*10);
        System.out.println("Lapso de tiempo "+time+"\n");
        Scheduler scheduler = new Scheduler(10,time);
        scheduler.start();
        HashMap<Long, Datos> datosTotales =       scheduler.getDatosRecolectados();
        imprimirDatosEnPantalla(datosTotales);
    }
    public static void imprimirDatosEnPantalla(HashMap<Long, Datos> map){
        System.out.println("Resultados de la implementación");
        Set<Long> keys = map.keySet();
        for(Long process_id : keys){
            Datos process_datos = map.get(process_id);
            System.out.println("Proceso " + process_id + "\n");
            System.out.println("Numero de rafagas E/S: " + process_datos.getnRES());
            for(Dato dato : process_datos.getDatos()){
                dato.imprimir("                 ");
            }
        }
    }
}
