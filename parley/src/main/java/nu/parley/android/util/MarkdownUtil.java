package nu.parley.android.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Block;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Code;
import org.commonmark.node.Document;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.ThematicBreak;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.core.CoreProps;

public final class MarkdownUtil {

    public static Spanned convert(Context context, String text) {
        Markwon markwon = Markwon.builder(context)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        super.configureSpansFactory(builder);

                        // Clear formatting for some stuff
                        builder.setFactory(Code.class, new EmptySpanFactory());
                        builder.setFactory(BlockQuote.class, new EmptySpanFactory());
                        builder.setFactory(Block.class, new EmptySpanFactory());
                        builder.setFactory(Image.class, new EmptySpanFactory());
                        builder.setFactory(HtmlBlock.class, new EmptySpanFactory());
                        builder.setFactory(HtmlInline.class, new EmptySpanFactory());
                        builder.setFactory(IndentedCodeBlock.class, new EmptySpanFactory());
                        builder.setFactory(Paragraph.class, new EmptySpanFactory());
                        builder.setFactory(SoftLineBreak.class, new EmptySpanFactory());
                        builder.setFactory(HardLineBreak.class, new EmptySpanFactory());
                        builder.setFactory(ThematicBreak.class, new EmptySpanFactory());
                        builder.setFactory(Document.class, new EmptySpanFactory());
                        builder.setFactory(FencedCodeBlock.class, new EmptySpanFactory());
                    }

                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                        super.configureVisitor(builder);

                        // Fixing headings
                        builder.on(Heading.class, new RevertHeadingsNodeVisitor());
                    }
                })
                .build();
        return markwon.toMarkdown(text);
    }

    private static class EmptySpanFactory implements SpanFactory {
        @Nullable
        @Override
        public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
            return new LeadingMarginSpan() {
                @Override
                public int getLeadingMargin(boolean first) {
                    return 0;
                }

                @Override
                public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

                }
            };
        }
    }

    private final static class RevertHeadingsNodeVisitor implements MarkwonVisitor.NodeVisitor<Heading> {
        @Override
        public void visit(@NonNull MarkwonVisitor visitor, @NonNull Heading heading) {
            CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());

            StringBuilder headingTags = new StringBuilder();
            for (int i = 0; i < heading.getLevel(); i++) {
                headingTags.append("#");
            }
            headingTags.append(" ");

            visitor.builder().append(headingTags.toString());
            visitor.visitChildren(heading);
            visitor.builder().append("\n");
        }
    }
}
