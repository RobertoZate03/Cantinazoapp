package mx.aqtiva.cantinazo.listas;

/**
 * Created by Roberto on 19/02/2019.
 */

public class ListaMesasPagadas {
    public String id, monto_total,mesa, fecha_venta,pagado, usuario_id,usuario_name, sucursal_id,sucursal_name, tipo_pago_id, tipo_pago_name, terminado;



    public ListaMesasPagadas(String id, String monto_total, String mesa, String fecha_venta, String pagado, String usuario_id, String usuario_name, String sucursal_id, String sucursal_name, String tipo_pago_id, String tipo_pago_name, String terminado) {
        this.id = id;
        this.monto_total = monto_total;
        this.mesa = mesa;
        this.fecha_venta = fecha_venta;
        this.pagado = pagado;
        this.usuario_id = usuario_id;
        this.usuario_name = usuario_name;
        this.sucursal_id = sucursal_id;
        this.sucursal_name = sucursal_name;
        this.tipo_pago_id = tipo_pago_id;
        this.tipo_pago_name = tipo_pago_name;
        this.terminado = terminado;
    }
}