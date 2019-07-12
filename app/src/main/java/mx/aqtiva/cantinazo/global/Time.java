package mx.aqtiva.cantinazo.global;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    public static String fecha(String tiempoPasado) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Date newDate = sdf.parse(tiempoPasado);
        sdf = new SimpleDateFormat("EEEE,MMMM,MM,dd,yyyy");
        String date = sdf.format(newDate);
        String fecha[] = date.split(",");
        String diaLetra = fecha[0];
        String mesLetra = fecha[1];
        String mes = fecha[2];
        String dia = fecha[3];
        String ano = fecha[4];
        return diaLetra + ", " + dia + " de " + mesLetra + " del " + ano;
    }

}