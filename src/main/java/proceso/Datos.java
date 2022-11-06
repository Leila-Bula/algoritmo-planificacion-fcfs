package proceso;

import java.util.ArrayList;
import java.util.List;

public class Datos {
    private List<Dato> datos;
    private int nRES;

    public Datos(int nRES) {
        this.datos = new ArrayList<Dato>();
        this.nRES = nRES;
    }

    public List<Dato> getDatos() {
        return datos;
    }

    public int getnRES() {
        return nRES;
    }

    public void add(TypeCambio typeCambio){
        datos.add(new Dato(typeCambio));
    }
}
