

import java.lang.reflect.Array;
import java.time.LocalDate;

public class Datum {

    public final static int FORMAT_SHORT = 0;
    public final static int FORMAT_NORMAL = 1;
    public final static int FORMAT_LONG = 2;
    public final static int FORMAT_US = 3;

    private int tag = 0;
    private int monat = 0;
    private int jahr = 0;

    /**
     * Erzeugt eine Datumsinstanz mit dem aktuellen Systemdatum.
     */
    public Datum() {
        LocalDate today = LocalDate.now();
        tag = today.getDayOfMonth();
        monat = today.getMonthValue();
        jahr = today.getYear();
    }

    /**
     * Erzeugt eine Datumsinstanz im Format TT.MM.YYYY.
     *
     * @param dateString zu parsender String
     */
    public Datum(String dateString) {
        String[] date = dateString.split("\\.");
        try {
            if (date.length!=3 || Integer.parseInt(date[2])<1900 || Integer.parseInt(date[1])>12 || Integer.parseInt(date[0])>=getMonatsLength(Integer.parseInt(date[1])-1, Integer.parseInt(date[2]))) {
                throw new IllegalArgumentException("" + dateString);
            }

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(""+dateString);
        }
        tag = Integer.parseInt(date[0]);
        monat = Integer.parseInt(date[1]);
        jahr = Integer.parseInt(date[2]);
    }

    /**
     * Erzeugt eine Datumsinstanz, die t Tage nach dem 1.1.1900 liegt.
     *
     * @param tage die Tage seit dem 1.1.1900; muss >= 0 sein
     */
    public Datum(int tage) {
        if (tage<0) {
            throw new IllegalArgumentException();
        }
        Datum damals = new Datum(1, 1, 1900);
        addiereTage(tage);
        tag = damals.tag;
        monat = damals.monat;
        jahr = damals.jahr;
    }

    /**
     * Erzeugt eine Datumsinstanz mit den gegebenen Werten.
     *
     * @param tag   der Tag 1-31 ( abhaengig vom Monat)
     * @param monat das Monat, 1 - 12
     * @param jahr  das Jahr, 1900 - 3000
     */
    public Datum(int tag, int monat, int jahr) {
        this.tag = tag;
        this.monat = monat;
        this.jahr = jahr;
    }

    /**
     * Liefert die zwischen zwei Daten vergangenen Tage.
     *
     * @param d1 das erste Datum
     * @param d2 das zweite Datum
     * @return Tage zwischen <code>d1</code> und <code>d2</code>;
     * positiv wenn <code>d2</code> nach <code>d1</code> liegt, sonst negativ
     */
    public static int getMonatsLength(int m, int j) {
        if (m==1 && isSchaltjahr(j)) {
            return 29;
        }
        return switch (m) {
            case 0 -> 31;
            case 1 -> 28;
            case 2 -> 31;
            case 3 -> 30;
            case 4 -> 31;
            case 5 -> 30;
            case 6 -> 31;
            case 7 -> 31;
            case 8 -> 30;
            case 9 -> 31;
            case 10 -> 30;
            case 11 -> 31;

            default -> throw new IllegalStateException("Unexpected value: " + m);
        };
    }
    public static int tageZwischen(Datum d1, Datum d2) {
        int zwischen = 0;
        return 0;
    }

    /**
     * Prüft auf Schaltjahr.
     *
     * @param jahr die zu prüfende Jahreszahl
     * @return <code>true</code>, wenn <code>jahr</code> ein Schaltjahr ist, <code>false</code> sonst
     */
    public static boolean isSchaltjahr(int jahr) {
        if (jahr%4==0 && (jahr%100!=0 || jahr%400==0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Datum datum = (Datum) o;
        return tag == datum.tag &&
                monat == datum.monat &&
                jahr == datum.jahr;
    }

    /**
     * Liefert den Namen des Monats.
     *
     * @return den Monatsnamen
     */
    public String getMonatAsString() {
        return switch (this.monat) {
            case 1 -> "Januar";
            case 2 -> "Februar";
            case 3 -> "März";
            case 4 -> "April";
            case 5 -> "Mai";
            case 6 -> "Juni";
            case 7 -> "Juli";
            case 8 -> "August";
            case 9 -> "Sepember";
            case 10 -> "Oktober";
            case 11 -> "November";
            case 12 -> "Dezember";

            default -> throw new IllegalStateException("Unexpected value: " + this.monat);
        };
    }

    /**
     * Erhöht (vermindert) das gespeicherte Datum. Liegt nach dieser
     * Operation das Datum vor dem 1.1.1900,
     * so wird eine IllegalArgumentException geworfen und keine Änderung durchgeführt.
     *
     * @param t die Tage, um die dieses Datum erhöht (t > 0) bzw. vermindert (t < 0) wird
     */
    public Datum addiereTage(Datum d, int t) {
        d.tag += t;
        while (d.tag/getMonatsLength(d.monat-1, d.jahr)>0) {
            d.monat += 1;
            if (d.monat>12) {
                d.jahr ++;
                d.monat = 1;
            }
            d.tag -= getMonatsLength(d.monat-1, d.jahr);
        }
        while (d.tag<0) {
            d.monat--;
            if (d.monat<1) {
                d.jahr--;
                if (d.jahr<1900) {
                    throw new IndexOutOfBoundsException(d.jahr);
                }
                d.monat = 12;
            }
            d.tag += getMonatsLength(d.monat+1, d.jahr);
        };
        return d;
    }
    public void addiereTage(int t) {
         addiereTage(this, t);
    }

    /**
     * Liefert die Nummer des Wochentages.
     *
     * @return die Nummer des Wochentages im Bereich von 0(Montag) bis 6(Sonntag)
     */
    public int wochentagNummer() {

    }

    /**
     * Liefert den Wochentag als String.
     *
     * @return den Wochentag als String
     */
    public String wochentag() {
        if (this.monat<=2) {
            int h = monat +12;
            int k = jahr -1;
        } else {
            int h = monat;
            int k = jahr;
        }
        return "";
    /**
     * Vergleicht das <code>this</code>-Datum mit dem übergebenen.
     *
     * @param d das Datum, mit dem verglichen wird
     * @return eine negative Zahl, wenn d spaeter liegt, positiv, wenn d frueher l i egt und
     * 0 bei gleichem Datum
     */
    public int compareTo(Datum d) {
        int erg = this.tag;
        erg -= d.tag;
        int monate = d.monat;
        while (monate>0) {
            erg += getMonatsLength(monate, d.jahr);
        }
        erg += jahreInTag(d.jahr);
        return erg;
    }
    public int jahreInTag(int jahre) {
        int tage = 0;
        while (jahre>0 ){
            if (isSchaltjahr(jahre)) {
                tage += 366;
            } else {
                tage += 365;
            }
        }
        return tage;
    }

    /**
     * Liefert eine Stringdarstellung i n der Form <code>tt.mm.jjjj</code>
     *
     * @return Stringdarstellung i n der Form <code>tt.mm.jjjj</code>QA QA
     * @override
     */
    @Override
    public String toString() {
        return System.out.format("%2f%1s%2f%1s%2f", tag, ".", monat, ".", jahr).toString();
    }

    /**
     * Liefert eine Stringdarstellung unterschiedlichen Formats
     *
     * @param format Moegliche Werte sind:
     *               <code>Datum.FORMAT_SHORT, Datum.FORMAT_NORMAL, Datum.FORMAT_LONG, Datum.FORMAT_US</code>
     * @return Datum im Format <code>dd.mm.yy</code> bei <code>Datum.FORMAT_SHORT</code>,
     * im Format <code>dd.monat jjjj, wochentag (Monat ausgeschrieben)</code> bei
     * <code>Datum.FORMAT_LONG</code>, im Format <code>jjjj/tt/mm</code> bei
     * <code>Datum.FORMAT_US</code>
     */
    public String toString(int format) {
        throw new UnsupportedOperationException("TODO");
    }
}
