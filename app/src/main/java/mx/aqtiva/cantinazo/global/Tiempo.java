package mx.aqtiva.cantinazo.global;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tiempo {

    String strDias = "";
    String strHoras = "";
    String strMinutos = "";
    String strSegundos = "";

    public String tiempo(String tiempoPasado, String tiempoPresente) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
        Date dateAntes = null;
        Date dateHoy = null;

        dateAntes = sdf.parse(tiempoPasado);
        dateHoy = sdf.parse(tiempoPresente);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(dateAntes);
        cal2.setTime(dateHoy);

        long milis1 = cal1.getTimeInMillis();
        long milis2 = cal2.getTimeInMillis();
        long diff = milis2 - milis1;
        long diffSeconds = diff / 1000;
        long diffMinutes = diffSeconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;

        long dias = diffDays;
        long horas = diffHours - (dias * 24);
        long minutos = diffMinutes - (dias * 24 * 60) - (horas * 60);
        long segundos = diffSeconds - (dias * 24 * 60 * 60) - (horas * 60 * 60) - (minutos * 60);

        strDias = dias + "";
        strHoras = horas + "";
        strMinutos = minutos + "";
        strSegundos = segundos + "";


        if (dias > 0) {
            return strDias + " días con " + strHoras + " horas y " + strMinutos + " minutos";
        } else {
            if (horas > 0) {
                return strHoras + " horas y " + strMinutos + " minutos";
            } else if (minutos > 0) {
                return minutos + " Minutos";
            }
            if (segundos > 0) {
                return segundos + " Segundos";
            } else {
                return "";
            }
        }
    }
}