package nu.parley.android.util;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

// Custom span to make links bold
public class BoldLinkSpan extends ClickableSpan {
    private final String url;

    public BoldLinkSpan(String url) {
        this.url = url;
    }

    @Override
    public void onClick(View widget) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(this.url));
        widget.getContext().startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Make links bold
        ds.setUnderlineText(true); // Keep underline
    }
}
