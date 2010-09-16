/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.units;

/**
 * The Unit class represents a physical unit<p />
 *
 * The model used is this:<br />
 * <i>inMainUnits = (ratio * inCurrentUnits + a) * x</i><br />
 * <i>inCurrentUnits = (inMainUnits - (a * x)) / (ratio * x)</i>
 * @author albus
 */
public class Unit {

    /*
     * Temperature
     */
    public static final Unit CELSIUS = new Unit("Celsius (C)", 1);
    public static final Unit FAHRENHEIT =
            new Unit("Fahrenheit (F)", 1, -32, 0.55556);
    public static final Unit DEGREE_OF_FROST =
            new Unit("Degree of frost",-0.55556);
    public static final Unit KELVIN =
            new Unit("Kelvin (K)", 1, -273.15, 1);
    
    /*
     * Length
     */
    public static final Unit MILLIMETRE = new Unit("Millimetre (mm)", 0.001);
    public static final Unit CENTIMETRE = new Unit("Centimetre (cm)", 0.01);
    public static final Unit DECIMETRE = new Unit("Decimetre (dm)", 0.1);
    public static final Unit METRE = new Unit("Metre (m)", 1);
    public static final Unit KILOMETRE = new Unit("Kilometre (km)", 1000);

    public static final Unit THOU = new Unit("Thou (mil)", 0.0000254);
    public static final Unit INCH = new Unit("Inch (in)", 0.0254);
    public static final Unit FOOT = new Unit("Foot (ft)", 0.3048);
    public static final Unit YARD = new Unit("Yard (yd)", 0.9144);
    public static final Unit FATHOM = new Unit("Fathom (fm)", 1.8288);
    public static final Unit CHAIN = new Unit("Chain (ch)", 20.11684);
    public static final Unit FURLONG = new Unit("Furlong (fur)", 201.168);
    public static final Unit MILE = new Unit("Mile (mi)", 1609.344);
    public static final Unit LEAGUE = new Unit("League (land)", 4828.032);

    public static final Unit CABLE = new Unit("Cable", 185.3184);
    public static final Unit NAUTICAL_MILE = new Unit("Nautical Mile (nmi)", 1852);

    public static final Unit ROD = new Unit("Rod (H)", 5.0292);

    /*
     * Area
     */
    public static final Unit SQUARE_MILLIMETRE =
            new Unit("Square Millimetre (mm2)", 0.000001);
    public static final Unit SQUARE_CENTIMETRE =
            new Unit("Square Centimetre (cm2)", 0.0001);
    public static final Unit SQUARE_DECIMETRE =
            new Unit("Square Decimetre (dc2)", 0.01);
    public static final Unit SQUARE_METRE = new Unit("Square Metre (m2)", 1);
    public static final Unit ARE = new Unit("Are (a)", 100);
    public static final Unit DECARE = new Unit("Decare (daa)", 1000);
    public static final Unit HECTARE = new Unit("Hectare (ha)", 10000);
    public static final Unit SQUARE_KILOMETRE = 
            new Unit("Square Kilometre (km2)", 1000000);
    
    public static final Unit SQUARE_FOOT = new Unit("Square Foot (sq ft)", 0.09290304);
    public static final Unit SQUARE_YARD = new Unit("Square Yard (sq yd)", 0.83612736);
    public static final Unit ACRE = new Unit("Acre (ac)", 4046.8564224);
    public static final Unit SQUARE_MILE =
            new Unit("Square Mile (sq mi)", 2589988.110336);
    /*
     * Volume
     */
    public static final Unit MILLILITRE = new Unit("Millilitre (mL)", 0.000001);
    public static final Unit LITRE = new Unit("Litre (L)", 0.001);

    public static final Unit CUBIC_MILLIMETRE  = 
            new Unit("Cubic Millimetre (mm3)", .000000001);
    public static final Unit CUBIC_CENTIMETRE = 
            new Unit("Cubic Centimetre (cm3)", 0.000001);
    public static final Unit CUBIC_DECIMETRE = 
            new Unit("Cubic Decimetre (dm3)", 0.001);
    public static final Unit CUBIC_METRE = new Unit("Cubic Metre (m3)", 1);

    public static final Unit FLUID_OUNCE = 
            new Unit("Fluid Ounce (fl oz)", 0.0000284131);
    public static final Unit GILL = new Unit("Gill (gi)", 0.000142065);
    public static final Unit PINT = new Unit("Pint (pt)", 0.000568261);
    public static final Unit QUART = new Unit("Quart (qt)", 0.00113652);
    public static final Unit GALLON = new Unit("Gallon (gal)", 0.00454609);

    /*
     * Mass
     */
    public static final Unit MILLIGRAM = new Unit("Milligram (mg)", 0.000001);
    public static final Unit GRAM = new Unit("Gram (g)", 0.001);
    public static final Unit KILOGRAM = new Unit("Kilogram (kg)", 1);
    public static final Unit TONNE = new Unit("Tonne (t)", 1000);

    public static final Unit GRAIN = new Unit("Grain (gr)", 0.0000647989);
    public static final Unit OUNCE = new Unit("Ounce (oz)", 0.0283495);
    public static final Unit POUND = new Unit("Pound (lb)", 0.45359237);
    public static final Unit STONE = new Unit("Stone (st)", 6.35029318);
    public static final Unit HUNDREDWEIGHT = 
            new Unit("Hundredweight (cwt)", 50.80234544);
    public static final Unit TON = new Unit("Ton (ton)",  1016.0469088);

    /*
     * Velocity
     */
    public static final Unit METRE_PER_SECOND =
            new Unit("Metre per second (m/s)", 3.6);
    public static final Unit KILOMETRE_PER_HOUR =
            new Unit("Kilometre per hour (km/h)", 1);

    public static final Unit FOOT_PER_SECOND =
            new Unit("Foot per second (fps)", 1.09728);
    public static final Unit MILE_PER_HOUR =
            new Unit("Mile per hour (mph)", 1.609344);

    public static final Unit KNOT = new Unit("Knot (kn)", 1.852);

    /*
     * Pressure
     */
    public static final Unit PASCAL = new Unit("Pascal (Pa)", 1);
    public static final Unit HECTOPASCAL = new Unit("Hectopascal (hPa)", 100);
    public static final Unit KILOPASCAL = new Unit("Kilopascal (kPa)", 1000);
    public static final Unit MILLIBAR = new Unit("Millibar (mbar)", 100);
    public static final Unit BAR = new Unit("Bar (bar)", 100000);
    public static final Unit MILLIMETRE_OF_MERCURY =
            new Unit("Millimetre of mercury (mmHg)", 133.322);
    public static final Unit ATMOSPHERE = 
            new Unit("Atmosphere (Atm)", 101325);
    public static final Unit PSI =
            new Unit("Pound-force per sq in (psi)", 6894);
    public static final Unit INCH_OF_MERCURY = 
            new Unit("Inch of mercury (inHg)", 3386.389);

    /*
     * Power
     */
    public static final Unit MILLIWATT = new Unit("Milliwatt (mW)", 0.001);
    public static final Unit WATT = new Unit("Watt (W)", 1);
    public static final Unit KILOWATT = new Unit("Kilowatt (kW)", 1000);
    public static final Unit MEGAWATT = new Unit("Megawatt (MW)", 1000000);

    public static final Unit HORSEPOWER = 
            new Unit("Horsepower (HPS)", 745.69987158227022);

    /*
     * Energy
     */
    public static final Unit WATT_HOUR = new Unit("Watt hour (W h)", 3600);
    public static final Unit KILOWATT_HOUR =
            new Unit("Kilowatt hour (kW h)", 3600000);
    public static final Unit JOULE = new Unit("Joule (J)", 1);
    public static final Unit CALORIE = new Unit("calorie (cal)", 4.1868);

    /*
     * Linear Density
     */
    public static final Unit DOTS_PER_INCH = 
            new Unit("Dots per inch (DPI)", 1);
    public static final Unit DOTS_PER_CENTIMETRE =
            new Unit("Dots per centimetre (dpcm)", 2.54);

    /*
     * Angle
     */
    public static final Unit RADIAN = new Unit("Radian (rad)", 1);
    public static final Unit DEGREE = new Unit("Degree (deg)", 0.0174532929);

    public final String name;
    private final double ratio;
    private final double a;
    private final double x;

    public Unit(final String name, final double ratio,
            final double a, final double x) {
        this.name = name;
        this.ratio = ratio;
        this.a = a;
        this.x = x;
    }

    public Unit(final String name, final double ratio) {
        this(name, ratio, 0, 1);
    }

    /**
     * Converts quantity from one unit to another unit
     * @param quantity  the quantity in the initial units
     * @param from      the initial units
     * @param to        the units after conversion
     * @return          the quantity in the units after conversion
     */
    public static double convert(double quantity, Unit from, Unit to) {
        return to.inSpecUnits(from.inBaseUnits(quantity));
    }

    private double inBaseUnits(double inSpecUnits) {
        return (ratio * inSpecUnits + a) * x;
    }

    private double inSpecUnits(double inBaseUnits) {
        return (inBaseUnits - (a * x)) / (ratio * x);
    }
}
