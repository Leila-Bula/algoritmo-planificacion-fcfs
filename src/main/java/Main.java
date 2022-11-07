import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import proceso.Dato;
import proceso.Datos;

class Main {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("ALGORITMO DE PLANIFICACIÓN FCFS\n");
    System.out.println("Ingrese el número de procesos con el que se probará el algoritmo\n");
    int n_procesos = sc.nextInt();
    System.out.println("A continuación ingrese el lapso de tiempo entre que se crea un proceso y otro\n");
    long time = sc.nextLong();
    Scheduler scheduler = new Scheduler(n_procesos, time);
    System.out.println("Iniciando ejecución\n");
    scheduler.start();
    HashMap<Long, Datos> datosTotales = scheduler.getDatosRecolectados();
    imprimirDatosEnPantalla(datosTotales, n_procesos);
  }

  public static void imprimirDatosEnPantalla(HashMap<Long, Datos> map, int num_process) {
    System.out.println("Resultados de la implementación\n");
    System.out.println("--Los siguientes tiempos están medidos en milisegundos-- \n");
    Set<Long> keys = map.keySet();
    double t_espera_total = 0;
    double t_espera_promedio;
    for (Long process_id : keys) {
      Datos process_datos = map.get(process_id);
      System.out.println("****** PROCESO " + process_id + " ******\n");
      System.out.println("Numero de ráfagas E/S: " + process_datos.getnRES());
      t_espera_total += calcularTiempos(process_datos);
    }
    t_espera_promedio = t_espera_total / num_process;
    System.out
        .println("El tiempo de espera promedio para el algoritmo fue de: " + t_espera_promedio + " milisegundos\n");
    System.out.println("\n\n\n\n");
    System.out.println("------MAS DETALLES------\n");
    for (Long process_id : keys) {
      System.out.println("PROCESO " + process_id + "\n");
      for (Dato dato : map.get(process_id).getDatos()) {
        dato.imprimir("                 ");
      }
    }
  }

  public static double calcularTiempos(Datos process_datos) {
    long t_espera_total = 0;
    long t_bloqueado_total = 0;
    long t_respuesta = 0;
    long t_ejecucion_total = 0;
    long ult_bloqueado = 0;
    long ult_listo = 0;
    long ult_ejecutando = 0;
    long n_RES = process_datos.getnRES();
    long n_REj = n_RES + 1;
    boolean swiche = true;
    for (Dato dato : process_datos.getDatos()) {
      switch (dato.getTypeCambio().toString()) {
        case "NUEVO_LISTO": {
          ult_listo = dato.getTime().getTime();
          t_respuesta = ult_listo - process_datos.getCreated_at().getTime();
          break;
        }
        case "BLOQUEADO_LISTO": {
          ult_listo = dato.getTime().getTime();
          t_bloqueado_total += (ult_listo - ult_bloqueado);
          break;
        }
        case "LISTO_EJECUTANDO": {
          ult_ejecutando = dato.getTime().getTime();
          t_espera_total += (ult_ejecutando - ult_listo);
          if (swiche) {
            t_respuesta += t_espera_total;
          }
          break;
        }
        case "EJECUTANDO_TERMINADO": {
          t_ejecucion_total += (dato.getTime().getTime() - ult_ejecutando);
          if (swiche) {
            t_respuesta += t_ejecucion_total;
            swiche = false;
          }
          break;
        }
        case "EJECUTANDO_LISTO": {
          ult_listo = dato.getTime().getTime();
          t_ejecucion_total += (ult_listo - ult_ejecutando);
          break;
        }
        case "EJECUTANDO_BLOQUEADO": {
          ult_bloqueado = dato.getTime().getTime();
          t_ejecucion_total += (ult_bloqueado - ult_ejecutando);
          if (swiche) {
            t_respuesta += t_ejecucion_total;
            swiche = false;
          }
          break;
        }
      }
    }
    double t_espera_prom = ((double) t_espera_total) / ((double) n_REj);
    double t_bloqueado_prom = 0;
    if (n_RES > 0) {
      t_bloqueado_prom = ((double) t_bloqueado_total) / ((double) n_RES);
    }
    long tiempo = t_ejecucion_total + t_bloqueado_total + t_espera_total;
    System.out.println("Tiempo de ejecución total: " + t_ejecucion_total + "\n");
    System.out.println("Tiempo de respuesta: " + t_respuesta + "\n");
    System.out.println("Tiempo de espera total: " + t_espera_total + "\n");
    System.out.println("Tiempo de espera promedio: " + t_espera_prom + "\n");
    System.out.println("Tiempo total en estado bloqueado: " + t_bloqueado_total + "\n");
    System.out.println("Promedio de tiempo bloqueado: " + t_bloqueado_prom + "\n");
    System.out.println("Tiempo total: " + tiempo + "\n");
    return t_espera_prom;
  }

  @Override
  public String toString() {
    return "Main []";
  }
}
