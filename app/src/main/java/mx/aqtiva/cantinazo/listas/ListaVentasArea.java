package mx.aqtiva.cantinazo.listas;

/**
 * Created by Roberto on 19/02/2019.
 */

public class ListaVentasArea {
    public String fecha, producto_id, producto, total, area;



    public ListaVentasArea(String fecha, String producto_id, String producto, String total, String area) {
        this.fecha = fecha;
        this.producto_id = producto_id;
        this.producto = producto;
        this.total = total;
        this.area = area;

    }
}
