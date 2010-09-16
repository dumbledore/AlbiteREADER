/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.units;

/**
 *
 * @author albus
 */
public class UnitGroup {

    /*
     * Units in groups
     */
    private static final Unit[] UNITS_TEMPERATURE = {
        Unit.CELSIUS, Unit.FAHRENHEIT, Unit.DEGREE_OF_FROST, Unit.KELVIN
    };

    private static final Unit[] UNITS_LENGTH = {
        Unit.MILLIMETRE, Unit.CENTIMETRE, Unit.DECIMETRE, Unit.METRE,
        Unit.KILOMETRE,

        Unit.THOU, Unit.INCH, Unit.FOOT, Unit.YARD, Unit.MILE,

        Unit.FATHOM, Unit.CHAIN, Unit.FURLONG, Unit.LEAGUE,

        Unit.CABLE, Unit.NAUTICAL_MILE,

        Unit.ROD
    };

    private static final Unit[] UNITS_AREA = {
        Unit.SQUARE_MILLIMETRE, Unit.SQUARE_CENTIMETRE, Unit.SQUARE_DECIMETRE,
        Unit.SQUARE_METRE, Unit.ARE, Unit.DECARE, Unit.HECTARE,
        Unit.SQUARE_KILOMETRE,

        Unit.SQUARE_FOOT, Unit.SQUARE_YARD, Unit.ACRE, Unit.SQUARE_MILE
    };

    private static final Unit[] UNITS_VOLUME = {
        Unit.MILLILITRE, Unit.LITRE,

        Unit.CUBIC_MILLIMETRE, Unit.CUBIC_CENTIMETRE, Unit.CUBIC_DECIMETRE,
        Unit.CUBIC_METRE,

        Unit.FLUID_OUNCE, Unit.GILL, Unit.PINT, Unit.QUART, Unit.GALLON
    };

    private static final Unit[] UNITS_MASS = {
        Unit.MILLIGRAM, Unit.GRAM, Unit.KILOGRAM, Unit.TONNE,

        Unit.GRAIN, Unit.OUNCE, Unit.POUND, Unit.STONE, Unit.HUNDREDWEIGHT,
        Unit.TON
    };

    private static final Unit[] UNITS_VELOCITY = {
        Unit.METRE_PER_SECOND, Unit.KILOMETRE_PER_HOUR,

        Unit.FOOT_PER_SECOND, Unit.MILE_PER_HOUR,

        Unit.KNOT
    };

    private static final Unit[] UNITS_PRESSURE = {
        Unit.PASCAL, Unit.HECTOPASCAL, Unit.KILOPASCAL,

        Unit.MILLIBAR, Unit.BAR, Unit.MILLIMETRE_OF_MERCURY,
        Unit.ATMOSPHERE, Unit.PSI, Unit.INCH_OF_MERCURY
    };

    private static final Unit[] UNITS_POWER = {
        Unit.MILLIWATT, Unit.WATT, Unit.KILOWATT, Unit.MEGAWATT,

        Unit.HORSEPOWER
    };

    private static final Unit[] UNITS_ENERGY = {
        Unit.WATT_HOUR, Unit.KILOWATT_HOUR, Unit.JOULE, Unit.CALORIE
    };

    private static final Unit[] UNITS_LINEAR_DENSITY = {
        Unit.DOTS_PER_CENTIMETRE, Unit.DOTS_PER_INCH
    };

    private static final Unit[] UNITS_ANGLE = {
        Unit.DEGREE, Unit.RADIAN
    };

    /*
     * The groups themselves
     */
    public static final UnitGroup GROUP_TEMPERATURE =
            new UnitGroup("Temperature", UNITS_TEMPERATURE);

    public static final UnitGroup GROUP_LENGTH =
            new UnitGroup("Length", UNITS_LENGTH);

    public static final UnitGroup GROUP_AREA =
            new UnitGroup("Area", UNITS_AREA);

    public static final UnitGroup GROUP_VOLUME =
            new UnitGroup("Volume", UNITS_VOLUME);

    public static final UnitGroup GROUP_MASS =
            new UnitGroup("Mass ", UNITS_MASS);

    public static final UnitGroup GROUP_VELOCITY =
            new UnitGroup("Velocity", UNITS_VELOCITY);

    public static final UnitGroup GROUP_PRESSURE =
            new UnitGroup("Pressure", UNITS_PRESSURE);

    public static final UnitGroup GROUP_POWER =
            new UnitGroup("Power", UNITS_POWER);

    public static final UnitGroup GROUP_ENERGY =
            new UnitGroup("Energy", UNITS_ENERGY);

    public static final UnitGroup GROUP_LINEAR_DENSITY =
            new UnitGroup("Linear density", UNITS_LINEAR_DENSITY);

    public static final UnitGroup GROUP_ANGLE =
            new UnitGroup("Angle", UNITS_ANGLE);

    public static final UnitGroup[] GROUPS = {
        GROUP_TEMPERATURE,
        GROUP_LENGTH,
        GROUP_AREA,
        GROUP_VOLUME,
        GROUP_MASS,
        GROUP_VELOCITY,
        GROUP_PRESSURE,
        GROUP_POWER,
        GROUP_ENERGY,
        GROUP_LINEAR_DENSITY,
        GROUP_ANGLE
    };

    /*
     * Fields
     */
    public final String name;
    public final Unit[] units;

    public UnitGroup(String name, Unit[] units) {
        this.name = name;
        this.units = units;
    }
}
