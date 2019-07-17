package mx.aqtiva.cantinazo.listas;

/**
 * Created by Roberto on 19/02/2019.
 */

public class ListaInventario {
    public String nombre, costo, restante, sucursal;



    public ListaInventario(String nombre, String costo, String restante, String sucursal) {
        this.nombre = nombre;
        this.costo = costo;
        this.restante = restante;
        this.sucursal = sucursal;
    }
}
