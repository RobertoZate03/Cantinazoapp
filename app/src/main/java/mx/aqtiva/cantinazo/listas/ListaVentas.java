package mx.aqtiva.cantinazo.listas;

/**
 * Created by Roberto on 19/02/2019.
 */

public class ListaVentas {
    public String id, monto_total, mesa, fecha_venta, pagado, usuario_id, usuario, tipo_pago, hora_venta, terminado;



    public ListaVentas(String id, String monto_total, String mesa, String fecha_venta, String pagado, String usuario_id, String usuario, String tipo_pago, String hora_venta, String terminado) {
        this.id = id;
        this.monto_total = monto_total;
        this.mesa = mesa;
        this.fecha_venta = fecha_venta;
        this.pagado = pagado;
        this.usuario_id = usuario_id;
        this.usuario = usuario;
        this.tipo_pago = tipo_pago;
        this.hora_venta = hora_venta;
        this.terminado = terminado;

    }
}
