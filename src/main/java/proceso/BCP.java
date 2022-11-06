package proceso;

public class BCP {
    private long id;
    private Proceso proceso;
    private Datos datos;

    public BCP(long id) {
        this.proceso = new Proceso((int) Math.round(Math.random()*10));
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Datos getDatos() {
        return datos;
    }

    public void chargeProceso(){
        this.proceso.toListo();
        this.datos = new Datos(proceso.getNumRafES());
        this.datos.add(TypeCambio.NUEVO_LISTO);
    }

    public Estado getEstado(){
        return this.proceso.getEstado();
    }

    public Estado Run(){
        return this.proceso.run();
    }

    public void Block(){
        this.proceso.block();
        this.datos.add(TypeCambio.EJECUTANDO_BLOQUEADO);
    }

    public void Deblock(){
        this.proceso.deBlock();
        this.datos.add(TypeCambio.BLOQUEADO_LISTO);
    }

    public void Terminate(){
        this.proceso.terminate();
        this.datos.add(TypeCambio.BLOQUEADO_TERMINADO);
    }

    public void ToTerminate(){
        this.datos.add(TypeCambio.EJECUTANDO_TERMINADO);
    }

    public void ToEjecutando(){
        this.datos.add(TypeCambio.LISTO_EJECUTANDO);
    }

    public void Stop(){
        this.proceso.stop();
        this.datos.add(TypeCambio.EJECUTANDO_LISTO);
    }
}
