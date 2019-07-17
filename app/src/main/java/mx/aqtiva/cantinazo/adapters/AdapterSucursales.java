package mx.aqtiva.cantinazo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.listas.ListaSucursales;


public class AdapterSucursales extends ArrayAdapter<ListaSucursales> {

    LayoutInflater inflater;
    Context context;

    public AdapterSucursales(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.common_spinner_rows_drop, parent, false);
        final ListaSucursales object = getItem(position);
        TextView tvRow = row.findViewById(R.id.tvRow);
        tvRow.setTextColor(context.getResources().getColor(R.color.negro));
        if (object != null) {
            if(object.id.equals("")){
                tvRow.setText(object.nombre);
            }else{
                tvRow.setText(object.id + ".-" + object.nombre);
            }
            tvRow.setTextColor(context.getResources().getColor(R.color.negro));
        }
        return row;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.common_spinner_rows, parent, false);
        final ListaSucursales object = getItem(position);
        TextView tvRow = row.findViewById(R.id.tvRow);
        tvRow.setTextColor(context.getResources().getColor(R.color.negro));
        if (object != null) {
            if(object.id.equals("")){
                tvRow.setText(object.nombre);
            }else{
                tvRow.setText(object.id + ".-" + object.nombre);
            }
            tvRow.setTextColor(context.getResources().getColor(R.color.negro));
        }
        return row;
    }
}
