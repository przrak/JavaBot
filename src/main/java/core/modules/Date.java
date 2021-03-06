package core.modules;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Для работа с датой и временем
 *
 * @author Артур Куприянов
 * @version 1.0.0
 */
public class Date {

    static private Calendar cal;


    /**
     * Возвращает значение дня недели 1 - 7
     * @return День недели
     */
    public static int getDayOfWeek(){
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek - 1 == 0){
            return 7;
        }else{
            return dayOfWeek - 1;
        }
    }

    public static int getWeekOfYear(){
        Calendar cal = Calendar.getInstance();
        if (Date.getDayOfWeek() == 7){
        return cal.get(Calendar.WEEK_OF_YEAR) - 1;}
        else return cal.get(Calendar.WEEK_OF_YEAR);
    }


    /**
     * Увеличивает день на значение value, не может быть больше 7
     *
     * @param day начальное значение
     * @param value величина увеличения
     * @return увеличенный день недели
     */
    public static int increaseDayOfWeek(int day, int value){
        day += value%7;
        if (day > 7){
            day -= 7;
        }

        return day;
    }


    /**
     * Перегрузка метода {@link #increaseDayOfWeek(int, int)}<br>
     * Увеличивает значение дня на единицу
     * @param day начальное значение
     * @return увеличенный на единицу день недели
     */
    public static int increaseDayOfWeek(int day){
        return increaseDayOfWeek(day, 1);
    }

    /**
     *
     * @return dd.MM.yyyy HH:mm
     */
    public static String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return simpleDateFormat.format(new java.util.Date());
    }

    /**
     *
     * @return HH:mm
     */
    public static String getTimeNow(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(new java.util.Date());
    }

    public static void main(String[] args) {
        System.out.println(Date.getTimeNow());
        System.out.println(getWeekOfYear());
        System.out.println(getDayOfWeek());
    }
}
