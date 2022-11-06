package proceso;

public class Proceso {
    private Estado estado;
    private final int numRafES;
    private int sumRafES;

    public Proceso(int n_res){
        super();
        this.estado = Estado.NUEVO;
        this.numRafES = n_res;
    }

    public Estado run() {
        this.sumRafES = 0;
        int s = 0;
        while (this.sumRafES<this.numRafES){
            for(int i= 0; i<=((int) Math.round(Math.random()*10));i++){
                s+=2;
                if(s%3==0){
                    break;
                }
            }
            this.sumRafES++;
            this.block();
            return this.estado;
        }
        this.terminate();
        return this.estado;
    }

    public void block(){
        this.estado = Estado.BLOQUEADO;
    }

    public void deBlock(){
        this.estado = Estado.LISTO;
    }

    public void toListo(){
        this.estado = Estado.LISTO;
    }

    public void terminate(){
        if(this.sumRafES!=this.numRafES){
            this.sumRafES = this.numRafES;
        }
        this.estado = Estado.TERMINADO;
    }

    public void stop(){
        this.estado = Estado.LISTO;
    }
    public Estado getEstado() {
        return estado;
    }

    public int getNumRafES() {
        return numRafES;
    }
}
