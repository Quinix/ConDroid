package cz.quinix.condroid.annotations;

import android.os.Bundle;
import android.widget.TextView;
import cz.quinix.condroid.CondroidActivity;
import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;

public class ShowAnnotation extends CondroidActivity {
	
	private Annotation annotation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.annotation = (Annotation) this.getIntent().getSerializableExtra("annotation");
		this.setContentView(R.layout.annotation);
		
		TextView title = (TextView) this.findViewById(R.id.annot_title);
		title.setText(this.annotation.getTitle());
		
		TextView author = (TextView) this.findViewById(R.id.annot_author);
		author.setText(this.annotation.getAuthor());
		
		TextView info = (TextView) this.findViewById(R.id.annot_info);
		info.setText(DataProvider.getInstance(getApplicationContext()).getProgramLine(this.annotation.getLid()).getName()+
				", "+this.annotation.getPid()+", "+this.annotation.getLength()+", "+this.annotation.getType());
		
		TextView text = (TextView) this.findViewById(R.id.annot_text);
		text.setText(this.annotation.getAnnotation());
		
	}
}
