package mx.aqtiva.cantinazo.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.listas.ListaGastos;
import mx.aqtiva.cantinazo.listas.ListaInventario;

/**
 * Created by Roberto on 14/02/19.
 */
public class AdapterInventario extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    protected SharedPreferences sp;
    //Header header;
    List<ListaInventario> list;
    Context contx;

    public AdapterInventario(Context context, List<ListaInventario> headerItems) {
        this.list = headerItems;
        this.contx = context;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventario, parent, false);
            return new VHHeader(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHHeader) {
            ListaInventario currentItem = list.get(position);
            sp = PreferenceManager.getDefaultSharedPreferences(contx);
            VHHeader VHheader = (VHHeader) holder;
            if(position == 0){
                String tempString=currentItem.nombre;
                SpannableString spanString = new SpannableString(tempString);
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                VHheader.tvNombre.setText(spanString);
                VHheader.tvNombre.setTextColor(Color.parseColor("#000000"));

                String tempString1=currentItem.costo;
                SpannableString spanString1 = new SpannableString(tempString1);
                spanString1.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString1.length(), 0);
                VHheader.tvCosto.setText(spanString1);
                VHheader.tvCosto.setTextColor(Color.parseColor("#000000"));

                String tempString2=currentItem.restante;
                SpannableString spanString2 = new SpannableString(tempString2);
                spanString2.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString2.length(), 0);
                VHheader.tvRestante.setText(spanString2);
                VHheader.tvRestante.setTextColor(Color.parseColor("#000000"));

                String tempString3=currentItem.sucursal;
                SpannableString spanString3 = new SpannableString(tempString3);
                spanString3.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString3.length(), 0);
                VHheader.tvSucursal.setText(spanString3);
                VHheader.tvSucursal.setTextColor(Color.parseColor("#000000"));
                VHheader.vline.setVisibility(View.GONE);
            }else{
                VHheader.tvNombre.setText(currentItem.nombre);
                VHheader.tvCosto.setText(currentItem.costo);
                VHheader.tvRestante.setText(currentItem.restante);
                VHheader.tvSucursal.setText(currentItem.sucursal);
                VHheader.vline.setVisibility(View.GONE);
            }
            VHheader.itemView.setOnClickListener(v -> {

            });

        }
    }

    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {

        return list.get(position) instanceof ListaInventario;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCosto, tvRestante, tvSucursal;
        View vline;

        public VHHeader(View itemView) {
            super(itemView);
            this.tvNombre = itemView.findViewById(R.id.tvNombre);
            this.tvCosto = itemView.findViewById(R.id.tvCosto);
            this.tvRestante = itemView.findViewById(R.id.tvRestante);
            this.tvSucursal = itemView.findViewById(R.id.tvSucursal);
            this.vline = itemView.findViewById(R.id.vline);
        }
    }

}