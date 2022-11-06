package proceso;

import java.util.Date;

public class Dato {
    private TypeCambio typeCambio;
    private Date time;

    public Dato(TypeCambio typeCambio) {
        this.typeCambio = typeCambio;
        this.time = new Date();
    }

    public TypeCambio getTypeCambio() {
        return typeCambio;
    }

    public Date getTime() {
        return time;
    }

    public void imprimir(){
        switch (typeCambio){
            case NUEVO_LISTO -> System.out.println("Pasó de Nuevo a Listo\n");
            case BLOQUEADO_LISTO -> System.out.println("Pasó de Bloqueado a Listo\n");
            case BLOQUEADO_TERMINADO -> System.out.println("Pasó de Bloqueado a terminado\n");
            case LISTO_EJECUTANDO -> System.out.println("Pasó de Listo a ejecutando\n");
            case EJECUTANDO_TERMINADO -> System.out.println("Pasó de ejecutando a terminado\n");
            case EJECUTANDO_LISTO -> System.out.println("Pasó de ejecutando a Listo\n");
            case EJECUTANDO_BLOQUEADO -> System.out.println("Pasó de ejecutando a bloqueado\n");
        }
    }

    public void imprimir(String prefijo){
        System.out.println(prefijo);
        this.imprimir();
        System.out.println(prefijo + "Date: " + time.toString() + "\n");
        System.out.println(prefijo + "Milisegundos: " + time.getTime() + "\n" );
    }
}
