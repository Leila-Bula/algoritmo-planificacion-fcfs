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

    private boolean iniciado;
    private boolean swiche;

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
        this.iniciado = false;
        this.swiche = false;
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
        this.listos.add(process);
    }

    private void addBloqueados(BCP process){
        this.bloqueados.add(process);
    }

    private void addTerminados(BCP process){
        this.terminados.add(process);
    }

    private void addNuevos(BCP process){
        this.nuevos.add(process);
    }

    private void crearProceso(){
        BCP process = new BCP(this.contador+1);
        this.addNuevos(process);
        System.out.println("Creando proceso " + process.getId()+" con " + process.getRES() + " rafagas de E/S\n");
    }

    private void creadorProcesos(){
        this.crearProceso();
        CompletableFuture.runAsync(()->{
            this.cargadorProcesos();
        });
        this.contador++;
        while(this.contador<this.num_procesos){
            if(Math.round(Math.random()*10)>5){
                this.esperar(lapso);
            }
            this.crearProceso();
            CompletableFuture.runAsync(()->{
                this.cargadorProcesos();
            });
            this.contador++;
        }
    }

    private void cargadorProcesos(){
        if (this.nuevos.size() > 0) {
            BCP proceso = this.nuevos.get();
            proceso.chargeProceso();
            if(proceso.getEstado().equals(Estado.LISTO)){
                this.addListos(proceso);
                System.out.println("Cargando proceso " + proceso.getId()+"\n");
                if(!this.swiche){
                    this.swiche=true;
                }
            }
        }
    }

    private void eliminadorProcesos(){
        if(this.terminados.size()>0){
            BCP process = this.terminados.get();
            datosRecolectados.put(process.getId(),process.getDatos());
            //System.out.println("Eliminando proceso " + process.getId()+"\n");
        }
    }

    private void dispatcher(){
        if (this.listos.size() > 0) {
            this.ejecutando = this.listos.get();
            this.ejecutando.ToEjecutando();
        }else if(this.contadorCompletados<this.num_procesos){
            System.out.println("Esperando por proceso\n");
            while (this.listos.size()==0 && this.contadorCompletados<this.num_procesos && this.ejecutando == null){
                if(this.ejecutando==null){
                    this.esperarSegundos(5);
                }else{
                    break;
                }
            }
            if(this.contadorCompletados==this.num_procesos) {
                System.out.println("Ejecución terminada\n");
            }else{
                if (this.ejecutando==null){
                    this.ejecutando = this.listos.get();
                    this.ejecutando.ToEjecutando();
                }
            }
        }
    }

    private void ejecutarES(){
        BCP process = this.bloqueados.get();
        this.esperarSegundos(10);
        process.Deblock();
        this.addListos(process);
    }
    private void ejecutar(){
        if(!this.iniciado){
            this.iniciado = true;
            this.dispatcher();
            CompletableFuture.supplyAsync(() -> {
                //System.out.println("Proceso " + this.ejecutando.getId()+" a ejecucion\n");
                return this.ejecutando.Run();
            }).thenAcceptAsync((Estado estado) -> {
                BCP process = this.ejecutando;
                this.ejecutando = null;
                CompletableFuture.runAsync(()->{this.ejecutar();});
                if (estado.equals(Estado.TERMINADO)) {
                    this.contadorCompletados++;
                    //System.out.println("Proceso " + process.getId()+" terminado\n");
                    this.addTerminados(process);
                    CompletableFuture.runAsync(()->{
                        this.eliminadorProcesos();
                    });
                } else {
                    this.addBloqueados(process);
                    CompletableFuture.runAsync(()->{this.ejecutarES();});
                    //System.out.println("Proceso " + process.getId()+" bloqueado\n");
                }
            });
        }else if(this.num_procesos>this.contadorCompletados){
            this.dispatcher();
            CompletableFuture.supplyAsync(() -> {
                //System.out.println("Proceso " + this.ejecutando.getId()+" a ejecucion\n");
                return this.ejecutando.Run();
            }).thenAcceptAsync((Estado estado) -> {
                BCP process = this.ejecutando;
                this.ejecutando = null;
                CompletableFuture.runAsync(()->{this.ejecutar();});
                if (estado.equals(Estado.TERMINADO)) {
                    this.contadorCompletados++;
                    //System.out.println("Proceso " + process.getId()+" terminado\n");
                    this.addTerminados(process);
                    CompletableFuture.runAsync(()->{
                        this.eliminadorProcesos();
                    });
                } else {
                    this.addBloqueados(process);
                    CompletableFuture.runAsync(()->{this.ejecutarES();});
                    //System.out.println("Proceso " + process.getId()+" bloqueado\n");
                }
            });
        }else if(this.num_procesos==this.contadorCompletados){
            System.out.println("Ejecución terminada\n");
        }
    }

    public void start(){
        CompletableFuture creadorProcesosE = CompletableFuture.runAsync(()->{
            this.creadorProcesos();
        });
        while (!this.swiche){
            esperar(1);
        }
        this.ejecutar();
        while (this.num_procesos>this.contadorCompletados){
            esperar(1);
            if(this.num_procesos==this.contadorCompletados){
                break;
            }
        }
    }

    public HashMap<Long, Datos> getDatosRecolectados() {
        return datosRecolectados;
    }
}
