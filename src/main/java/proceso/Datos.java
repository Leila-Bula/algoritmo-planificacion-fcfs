package proceso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Datos {
    private List<Dato> datos;
    private int nRES;
    private Date created_at;

    public Datos(int nRES, Date created_at) {
        this.nRES = nRES;
        this.created_at = created_at;
        this.datos = new ArrayList<Dato>();
    }

    public List<Dato> getDatos() {
        return datos;
    }

    public int getnRES() {
        return nRES;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void add(TypeCambio typeCambio){
        datos.add(new Dato(typeCambio));
    }
}
