#import "content.typ": *;

#let styleColor(value) = {
    if value == auto {
        value = color.black;
    };
    return value.to-hex();
};

#let styleLength(value) = {
    if value == auto {
        value = 1pt;
    };
    return repr(value);
};

#let styleRatio(value) = {
    if value == auto {
        value = 100%;
    };
    return repr(value);
};

#let styleRelative(value) = {
    if value == 0 {
        return "0";
    };
    if type(value) == fraction {
        return repr(value);
    };
    value += 0%+0pt;
    if value == 0%+0pt {
        return "0";
    };
    if value.ratio == 0% {
        return styleLength(value.length);
    };
    if value.length == 0pt {
        return styleRatio(value.ratio);
    };
    return "calc(" + repr(value) + ")";
};

#let styleStroke(value) = {
    if value == auto {
        value = 1pt + black;
    };
    return styleLength(value.thickness)+" solid "+styleColor(value.paint);
};

#let strStyle(value) = {
    return value.pairs().map(((k, v))=>k+": "+v+";\n").sum(default: "");
};

#let mapBoxDic(dic) = {
    if dic.keys().contains("x") {
        if not dic.keys().contains("top") {
            dic.insert("top", dic.at("x"));
        };
        if not dic.keys().contains("bottom") {
            dic.insert("bottom", dic.at("x"));
        };
    };
    if dic.keys().contains("y") {
        if not dic.keys().contains("left") {
            dic.insert("left", dic.at("y"));
        };
        if not dic.keys().contains("right") {
            dic.insert("right", dic.at("y"));
        };
    };
    if dic.keys().contains("rest") {
        if not dic.keys().contains("top") {
            dic.insert("top", dic.at("rest"));
        };
        if not dic.keys().contains("bottom") {
            dic.insert("bottom", dic.at("rest"));
        };
        if not dic.keys().contains("left") {
            dic.insert("left", dic.at("rest"));
        };
        if not dic.keys().contains("right") {
            dic.insert("right", dic.at("rest"));
        };
    };
    dic.remove("x", default: none);
    dic.remove("y", default: none);
    dic.remove("rest", default: none);
    return dic;
};

#let styleCommon(c) = {
    let style = (:);
    if c.width != auto {
        style.insert("width", styleRelative(c.width));
    };
    if c.height != auto {
        style.insert("height", styleRelative(c.height));
    };
    if c.stroke != none {
        if type(c.stroke) == dictionary {
            for (k, v) in mapBoxDic(c.stroke).pairs() {
                style.insert("border-"+k, styleStroke(v));
            };
        } else {
            style.insert("border", styleStroke(c.stroke));
        }
    };
    if c.inset != none {
        if type(c.inset) == dictionary {
            for (k, v) in mapBoxDic(c.inset).pairs() {
                style.insert("padding-"+k, styleRelative(v));
            };
        } else {
            style.insert("padding", styleRelative(c.inset));
        };
    };
    return style;
};

#let stackEx(..children, dir: ttb, spacing: none) = {
    children = children.pos();
    let style = (:);
    style.insert("flex-direction",
        if dir == ltr {
            "row";
        } else if dir == rtl {
            "row-reverse";
        } else if dir == ttb {
            "column";
        } else if dir == btt {
            "column-reverse";
        } else {
            panic();
        }
    );
    if spacing!=0 {
        style.insert("gap", styleRelative(spacing));
    };
    return html_elem("div", attrs: ("class": "container stack", "style": strStyle(style)), children.join());
}

#let templateStyle(con) = if not isHtml {
    con;
} else [
    #show box: c => {
        let style = styleCommon(c);
        // TODO
        return html_elem("div", attrs: ("class": "container box", "style": strStyle(style)), c.body)
    };
    #show block: c => {
        if c.body == auto {
            panic(c);
        }
        let style = styleCommon(c);
        // TODO
        return html_elem("div", attrs: ("class": "container block", "style": strStyle(style)), c.body)
    };
    #show stack: c => stackEx(dir: c.dir, spacing: c.spacing, ..c.children);
    #show repeat: [...]; // TODO
    #show grid: c=>c.children.sum(); // TODO
    #show grid.cell: c=>c.body; // TODO
    #show math.equation: html_frame
    #importStyle("style.css", "lib/");
    #con;
];