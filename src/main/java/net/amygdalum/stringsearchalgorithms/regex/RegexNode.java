package net.amygdalum.stringsearchalgorithms.regex;

public interface RegexNode extends Cloneable {

	<T> T accept(RegexNodeVisitor<T> visitor);

	RegexNode clone();
	
}
