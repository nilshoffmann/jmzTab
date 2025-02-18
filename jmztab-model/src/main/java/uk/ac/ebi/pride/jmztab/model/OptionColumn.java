package uk.ac.ebi.pride.jmztab.model;

/**
 * Additional columns can be added to the end of the protein table. These column headers MUST start with the prefix "opt_".
 * Column names MUST only contain the following characters: 'A'-'Z', 'a'-'z', '0'-'9', '_', '-', '[', ']', and ':'.
 *
 * @author qingwei
 * @since 28/05/13
 */
public class OptionColumn extends MZTabColumn {
    public static final String OPT = "opt";
    public static final String GLOBAL = "global";

    /**
     * Get the optional column header, which start with the prefix "opt_".
     * the format: opt_{IndexedElement[id]}_{name}. Spaces within the parameter's name MUST be replaced by '_'.
     *
     * @param element if the name relates to all replicates, we use "global" in header. Here, if user set element is null for
                define for all replicates.
     * @param name SHOULD NOT be empty.
     * @return the header string used to identify this column in mzTab.
     */
    public static String getHeader(IndexedElement element, String name) {
        if (MZTabUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Optional column's value should not be empty.");
        }

        return OPT + "_" + (element == null ? GLOBAL : element.getReference()) +
                "_" + name.replaceAll(" ", "_");
    }

    /**
     * Create a optional column. Which header start with the prefix "opt_", logical position always stay the end of table.
     *
     * @see #getHeader() generate optional column header.
     *
     * @param element if the name relates to all replicates, we use "global" in header. Here, if user set element is null for
                define for all replicates.
     * @param name SHOULD NOT be empty.
     * @param columnType SHOULD NOT be empty.
     * @param offset SHOULD be positive integer.
     */
    public OptionColumn(IndexedElement element, String name, Class columnType, int offset) {
        super(getHeader(element, name), columnType, true, offset + 1 + "");
    }
}
