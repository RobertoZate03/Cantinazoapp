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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mx.aqtiva.cantinazo.R;
import mx.aqtiva.cantinazo.listas.ListaMesasActiva;
import mx.aqtiva.cantinazo.listas.ListaMesasPagadas;

/**
 * Created by Roberto on 14/02/19.
 */
public class AdapterMesasPagadas extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    protected SharedPreferences sp;
    //Header header;
    List<ListaMesasPagadas> list;
    Context contx;

    public AdapterMesasPagadas(Context context, List<ListaMesasPagadas> headerItems) {
        this.list = headerItems;
        this.contx = context;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mesa, parent, false);
            return new VHHeader(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHHeader) {
            ListaMesasPagadas currentItem = list.get(position);
            sp = PreferenceManager.getDefaultSharedPreferences(contx);
            VHHeader VHheader = (VHHeader) holder;
            VHheader.tvMesa.setText("Mesa " + currentItem.mesa);
            VHheader.tvTotal.setText("$" + currentItem.monto_total);
            if(position == 0){
                SpannableString spanString = new SpannableString("MESAS ACTIVAS");
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                VHheader.tvMesa.setText(spanString);
                VHheader.tvMesa.setTextColor(Color.parseColor("#000000"));

                SpannableString spanString1 = new SpannableString("TOTAL");
                spanString1.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString1.length(), 0);
                VHheader.tvTotal.setText(spanString1);
                VHheader.tvTotal.setTextColor(Color.parseColor("#000000"));

                VHheader.ivMesa.setVisibility(View.GONE);
            }else{
                VHheader.ivMesa.setVisibility(View.VISIBLE);
            }
            VHheader.btnVer.setVisibility(View.GONE);
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

        return list.get(position) instanceof ListaMesasPagadas;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView tvMesa, tvTotal;
        ImageView ivMesa;
        Button btnVer;

        public VHHeader(View itemView) {
            super(itemView);
            this.tvMesa = itemView.findViewById(R.id.tvMesa);
            this.tvTotal = itemView.findViewById(R.id.tvTotal);
            this.ivMesa = itemView.findViewById(R.id.ivMesa);
            this.btnVer = itemView.findViewById(R.id.btnVer);
        }
    }

}