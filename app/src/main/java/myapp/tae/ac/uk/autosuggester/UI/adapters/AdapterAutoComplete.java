package myapp.tae.ac.uk.autosuggester.UI.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import myapp.tae.ac.uk.autosuggester.R;

/**
 * Created by Karma on 10/06/16.
 */
public class AdapterAutoComplete extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<String> autoSuggestions = null;
    private ArrayList<String> originalSuggestions;
    private static final String TAG = "AdapterAutoComplete";
    private boolean isValueChange = false;

    public AdapterAutoComplete(Context context, ArrayList<String> autoSuggestions) {
        this.context = context;
        this.autoSuggestions = autoSuggestions;
        this.originalSuggestions = autoSuggestions;
    }

    public void setAutoSuggestions(ArrayList<String> suggestions){
        originalSuggestions = suggestions;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return autoSuggestions.size();
    }

    @Override
    public String getItem(int position) {
        return autoSuggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView tvListItem;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.autocomplete_list_item_layout, parent, false);
        }
        tvListItem = (TextView) convertView.findViewById(R.id.tvAutocompleteList);
        tvListItem.setText(autoSuggestions.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint == null) {
                    return null;
                }
                FilterResults results = new FilterResults();
                String suggestionWord;
                ArrayList<String> filteredList = new ArrayList<>();
                for (int i = 0; i < originalSuggestions.size(); i++) {
                    suggestionWord = originalSuggestions.get(i).toLowerCase();
                    if( suggestionWord.contains(constraint.toString().toLowerCase())){
                        filteredList.add(suggestionWord);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(constraint!=null&& results.count>0){
                    autoSuggestions = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }else
                    notifyDataSetInvalidated();
            }
        };
        return filter;
    }

}
