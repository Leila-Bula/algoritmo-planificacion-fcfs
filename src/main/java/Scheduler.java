import proceso.BCP;
import proceso.Datos;
import proceso.Estado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Scheduler {
    private final long lapso;
    private final int num_procesos;
    private int contador;
    private int contadorCompletados;
    private Cola<BCP> nuevos;
    private Cola<BCP> listos;
    private Cola<BCP> bloqueados;
    private Cola<BCP> terminados;
    private BCP ejecutando;
    private List opES;

    private HashMap<Long, Datos> datosRecolectados;

    public Scheduler(int num_procesos, long lapso) {
        this.num_procesos = num_procesos;
        this.lapso = lapso;
        this.nuevos = new Cola<BCP>();
        this.listos = new Cola<BCP>();
        this.bloqueados = new Cola<BCP>();
        this.terminados = new Cola<BCP>();
        this.opES = new ArrayList<>();
        this.datosRecolectados = new HashMap<>();
        this.contador = 0;
        this.contadorCompletados = 0;
    }

    private void esperar(long milisegundos){
        try {
            Thread.sleep (milisegundos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void esperarSegundos (long segundos) {
        try {
            Thread.sleep (segundos*10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BCP getEnEjecucion() {
        return ejecutando;
    }

    private void addListos(BCP process){
        listos.add(process);
    }

    private void addBloqueados(BCP process){
        bloqueados.add(process);
    }

    private void addTerminados(BCP process){
        terminados.add(process);
    }

    private void addNuevos(BCP process){
        nuevos.add(process);
    }

    private void crearProceso(){
        BCP process = new BCP(this.contador+1);
        this.addNuevos(process);
        System.out.println("Creando proceso " + process.getId()+"\n");
    }

    private void creadorProcesos(){
        this.crearProceso();
        contador++;
        while(contador<num_procesos){
            if(Math.round(Math.random()*10)>5){
                esperar(lapso);
            }
            this.crearProceso();
            contador++;
        }
    }

    private void cargadorProcesos(){
        while(contador<num_procesos) {
            while (nuevos.size() > 0) {
                BCP proceso = nuevos.get();
                proceso.chargeProceso();
                if(proceso.getEstado().equals(Estado.LISTO)){
                    this.addListos(proceso);
                    System.out.println("Cargando proceso " + proceso.getId()+"\n");
                }
            }
        }
    }

    private void eliminadorProcesos(){
        while (contadorCompletados<num_procesos){
            while(terminados.size()>0){
                BCP process = terminados.get();
                datosRecolectados.put(process.getId(),process.getDatos());
                System.out.println("Eliminando proceso " + process.getId()+"\n");
                contadorCompletados++;
            }
        }
    }

    private boolean dispatcher(){
        if(ejecutando==null){
            if(listos.size()>0){
                ejecutando = listos.get();
                ejecutando.ToEjecutando();
                return true;
            }
        }
        return false;
    }

    public void start(){
        CompletableFuture creadorProcesosE = CompletableFuture.runAsync(()->{
            this.creadorProcesos();
        });
        CompletableFuture cargadorProcesosE = CompletableFuture.runAsync(()->{
            this.cargadorProcesos();
        });
        CompletableFuture eliminadorProcesosE = CompletableFuture.runAsync(()->{
            this.eliminadorProcesos();
        });
        while (contadorCompletados<num_procesos) {
            while ((contadorCompletados < num_procesos) && (ejecutando == null)) {
                boolean swiche = this.dispatcher();
                if (swiche) {
                    CompletableFuture.supplyAsync(() -> {
                        return this.ejecutando.Run();
                    }).thenApplyAsync((Estado estado) -> {
                        BCP process = ejecutando;
                        ejecutando = null;
                        if (estado.equals(Estado.TERMINADO)) {
                            System.out.println("Proceso " + process.getId()+" terminado\n");
                            this.addTerminados(process);
                            return null;
                        } else {
                            this.addBloqueados(process);
                            System.out.println("Proceso " + process.getId()+" bloqueado\n");
                            return process;
                        }
                    }).thenAcceptAsync((BCP process) -> {
                        if (process != null) {
                            long segundos = Math.round(Math.random() * 10);
                            this.esperarSegundos(segundos);
                            process.Deblock();
                            this.addListos(process);
                            System.out.println("Pasando proceso " + process.getId()+"de bloqueado a listo\n");
                            this.bloqueados.getOut(process);
                        }
                    });
                }
            }
        }
    }

    public HashMap<Long, Datos> getDatosRecolectados() {
        return datosRecolectados;
    }
}
