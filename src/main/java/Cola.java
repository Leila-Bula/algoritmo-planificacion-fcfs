import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cola<element> {
    private List list;

    public Cola(){
        this.list = new ArrayList<element>();
    }

    public boolean add(element nuevo){
        return this.list.add(nuevo);
    }

    public element get(){
        return (element) this.list.remove(0);
    }

    public int size(){
        return this.list.size();
    }

    public boolean getOut(element e){
        return  !Objects.isNull(this.list.remove(e));
    }
}
