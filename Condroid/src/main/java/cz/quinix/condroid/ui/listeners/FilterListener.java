package cz.quinix.condroid.ui.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.ICondition;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.database.SearchProvider;
import cz.quinix.condroid.database.SearchQueryBuilder;
import cz.quinix.condroid.model.ProgramLine;
import cz.quinix.condroid.ui.ProgramActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 24.4.12
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
public class FilterListener {
    private ProgramActivity activity;

    public FilterListener(ProgramActivity activity) {
        this.activity = activity;

    }

    public void invoke() {
        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        ab.setTitle(R.string.chooseFilter);
        ab.setItems(R.array.filterBy, new FilterTypeSelected(activity, SearchProvider.getSearchQueryBuilder(TabListener.activeFragment.getClass().getName())));

        ab.create().show();
    }
}

class FilterTypeSelected implements DialogInterface.OnClickListener {

    private ProgramActivity activity;
    private SearchQueryBuilder search;

    public FilterTypeSelected(ProgramActivity activity, SearchQueryBuilder search) {
        this.activity = activity;
        this.search = search;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (which == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.dPickLine);

            Map<Integer, ProgramLine> pl = DataProvider.getInstance(activity).getProgramLines();
            int i = 0;
            final String[] pls = new String[pl.size()];

            for (ProgramLine p : pl.values()) {
                pls[i++] = p.getName();
            }
            Arrays.sort(pls);
            builder.setItems(pls, new ProgramLineFilter(activity, search, pls));

            builder.create().show();
        }

        if (which == 0) {
            AlertDialog.Builder build = new AlertDialog.Builder(activity);
            build.setTitle(R.string.dPickDate);
            List<Date> dates = DataProvider.getInstance(activity).getDates();
            if (dates.size() == 0) {
                Toast.makeText(activity, R.string.noDatesAvailable, Toast.LENGTH_LONG).show();
                return;
            }
            String[] ds = new String[dates.size()];
            int j = 0;


            DateFormat df = new SimpleDateFormat(
                    "EEEE d. M. yyyy", new Locale("cs",
                    "CZ"));

            for (Date date : dates) {
                char[] c = df.format(date).toCharArray();
                c[0] = Character.toUpperCase(c[0]);
                ds[j++] = new String(c);
            }
            build.setItems(ds, new DateFilter(activity, search, ds));
            build.create().show();
        }
        if (which == 2) {
            search.addParam(new ICondition() {
                @Override
                public String getCondition() {
                    String condition = "";
                    List<Integer> f = DataProvider.getInstance(activity).getFavorited();
                    if (f.size() > 0) {
                        for (Integer integer : f) {
                            if (!condition.equals("")) {
                                condition += ", ";
                            }
                            condition += integer.toString();
                        }
                        condition = "pid IN (" + condition + ")";
                    } else {
                        return "1=0";
                    }
                    return condition;
                }

                @Override
                public String getReadable() {
                    return "Oblíbené";
                }
            }, new Object().getClass().getName());
            activity.applySearch();
        }
    }
}


class ProgramLineFilter implements DialogInterface.OnClickListener {

    private ProgramActivity activity;
    private SearchQueryBuilder search;
    private String[] items;

    public ProgramLineFilter(ProgramActivity activity, SearchQueryBuilder search, String[] items) {
        this.activity = activity;
        this.search = search;
        this.items = items;
    }

    public void onClick(DialogInterface dialog, int which) {
        int lid = 0;
        String value = items[which];
        if (value
                .equals("- Zrušit filtr")) {
            search
                    .removeParam(new ProgramLine());
        } else {
            for (ProgramLine item : DataProvider.getInstance(activity)
                    .getProgramLines().values()) {
                if (item.getName().equals(value)) {
                    lid = item.getLid();
                    break;
                }
            }

            ProgramLine pl = new ProgramLine();
            pl.setLid(lid);
            pl.setName(value);
            search.addParam(pl);
        }
        activity.applySearch();
    }

}

class DateFilter implements DialogInterface.OnClickListener {

    private ProgramActivity activity;
    private SearchQueryBuilder search;
    private String[] items;

    public DateFilter(ProgramActivity activity, SearchQueryBuilder search, String[] items) {
        this.activity = activity;
        this.search = search;
        this.items = items;
    }

    public void onClick(DialogInterface dialog, int which) {

        DateFormat df = new SimpleDateFormat("EEEE d. M. yyyy", new Locale("cs", "CZ"));
        if (items[which].equals("- Zrušit filtr")) {
            search.removeParam(new Date());
        } else {
            try {
                Date d = df.parse(items[which]);
                search.addParam(d);
            } catch (ParseException e) {
            }
        }

        activity.applySearch();

    }
}