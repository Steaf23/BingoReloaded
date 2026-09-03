package io.github.steaf23.bingoreloadedcompanion.client.core;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CollapsibleTreeLayout extends AbstractLayout {

	public static final Identifier EXPAND_ITEMS_ICON = Identifier.parse("bingoreloadedcompanion:tree_expand");
	public static final Identifier COLLAPSE_ITEMS_ICON = Identifier.parse("bingoreloadedcompanion:tree_collapse");

	public static class Node {

		private final @Nullable Node parent;
		private final List<Node> children = new ArrayList<>();
		// layout is only displayed for leaf nodes
		private final @Nullable Layout layout;

		private Component text;
		private boolean collapsed = false;

		public Node(@Nullable Node parent, Component text, @Nullable Layout layout) {
			this.parent = parent;
			this.text = text;
			this.layout = layout;
		}

		public void setText(Component text) {
			this.text = text;
		}

		public boolean isAncestorOf(Node node) {
			if (parent == null) {
				return false;
			}
			return parent.isAncestorOf(node);
		}
	}

	private final Font font;
	private final Node root = new Node(null, Component.empty(), null);
	private final LinearLayout wrapped = LinearLayout.vertical();

	private boolean needsUpdate = false;
	private boolean canRebuild = true;

	public CollapsibleTreeLayout(Font font) {
		super(0, 0, 0, 0);
		this.font = font;
	}

	@Override
	public void visitChildren(@NonNull Consumer<LayoutElement> layoutElementVisitor) {
		canRebuild = false;

		wrapped.visitChildren(layoutElementVisitor);

		canRebuild = true;
		// is usually set during visitChildren
		if (needsUpdate) {
			rebuildTree();
		}
		needsUpdate = false;
	}

	@Override
	public void removeChildren() {
		wrapped.removeChildren(); // technically not needed, but whatever
		root.children.clear();
	}

	@Override
	public void arrangeElements() {
		this.wrapped.arrangeElements();
	}

	@Override
	public int getWidth() {
		return this.wrapped.getWidth();
	}

	@Override
	public int getHeight() {
		return this.wrapped.getHeight();
	}

	@Override
	public void setX(final int x) {
		this.wrapped.setX(x);
	}

	@Override
	public void setY(final int y) {
		this.wrapped.setY(y);
	}

	@Override
	public int getX() {
		return this.wrapped.getX();
	}

	@Override
	public int getY() {
		return this.wrapped.getY();
	}

	public void collapse(Node node) {
		setCollapsed(node, true);
	}

	public void expand(Node node) {
		setCollapsed(node, false);
	}

	public void toggleVisible(Node node) {
		setCollapsed(node, !node.collapsed);
	}

	public void setCollapsed(Node node, boolean collapse) {
		if (node.collapsed == collapse) {
			return;
		}

		node.collapsed = collapse;
		if (canRebuild) {
			rebuildTree();
		} else {
			needsUpdate = true;
		}
	}

	public void addTopLevelChild(LayoutElement element, LayoutSettings settings) {
		LinearLayout innerLayout = LinearLayout.vertical();
		innerLayout.addChild(element, settings);
		addNode(root, Component.empty(), innerLayout);
	}

	public Node addNode(@Nullable Node parent, Component text) {
		return addNode(parent, text, null);
	}

	public Node addNode(@Nullable Node parent, Component text, @Nullable Layout layout) {
		if (parent == null) {
			return addNode(root, text);
		}

		Node node = new Node(parent, text, layout);
		parent.children.add(node);
		rebuildTree();
		return node;
	}

	public Node addTopLevelNode(Component text, @Nullable Layout layout) {
		return addNode(root, text, layout);
	}

	public void rebuildTree() {
		wrapped.removeChildren();
		attachNode(root, -1);
		arrangeElements();
	}

	/**
	 * "Attaches" a node to the visual tree by adding its children and their layouts to appear under each other.
	 */
	private void attachNode(Node node, int depth) {
		if (!Component.empty().equals(node.text)) {
			CustomCheckbox box = treeNodeCheckbox(node.text, !node.collapsed, (_, pressed) -> {
				setCollapsed(node, !pressed);
			});
			wrapped.addChild(box, LayoutSettings.defaults().padding(2 + 16 * depth, 4, 2, 4));
		}

		if (node.collapsed) {
			return;
		}

		// Only leaf nodes can have visible layouts
		if (node.children.isEmpty() && node.layout != null) {
			wrapped.addChild(node.layout);
		}

		for (Node child : node.children) {
			attachNode(child, depth + 1);
		}
	}

	public CustomCheckbox treeNodeCheckbox(Component message, boolean startSelected, CustomCheckbox.OnValueChange callback) {
		return new CustomCheckbox(
				CustomCheckbox.getDefaultWidth(message, font),
				message,
				font,
				startSelected,
				EXPAND_ITEMS_ICON,
				COLLAPSE_ITEMS_ICON,
				callback);
	}
}
