package mx.aqtiva.cantinazo.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.listas.ListaGastos;

/**
 * Created by Roberto on 14/02/19.
 */
public class AdapterGastos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    protected SharedPreferences sp;
    //Header header;
    List<ListaGastos> list;
    Context contx;

    public AdapterGastos(Context context, List<ListaGastos> headerItems) {
        this.list = headerItems;
        this.contx = context;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
            return new VHHeader(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHHeader) {
            ListaGastos currentItem = list.get(position);
            sp = PreferenceManager.getDefaultSharedPreferences(contx);
            VHHeader VHheader = (VHHeader) holder;
            VHheader.tvMotivo.setText(currentItem.motivo);
            VHheader.tvCantidad.setText(currentItem.cantidad);
            if(list.size() == position + 1){
                String tempString=currentItem.motivo;
                SpannableString spanString = new SpannableString(tempString);
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                VHheader.tvMotivo.setText(spanString);
                VHheader.tvMotivo.setTextColor(Color.parseColor("#000000"));

                String tempString1=currentItem.cantidad;
                SpannableString spanString1 = new SpannableString(tempString1);
                spanString1.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString1.length(), 0);
                VHheader.tvCantidad.setText(spanString1);
                VHheader.tvCantidad.setTextColor(Color.parseColor("#000000"));
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

        return list.get(position) instanceof ListaGastos;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView tvMotivo, tvCantidad;

        public VHHeader(View itemView) {
            super(itemView);
            this.tvMotivo = itemView.findViewById(R.id.tvMotivo);
            this.tvCantidad = itemView.findViewById(R.id.tvCantidad);
        }
    }

}