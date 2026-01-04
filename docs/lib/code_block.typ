#import "./template.typ": *;

#let languageIcons = (
    "html": "html5",
    "css": "css3-alt",
    "javascript": "js",
    "c++": "cpp",
    "C#": "csharp",
    "go": "golang",
    "ts": "typescript",
    "vue": "vuejs"
);

#let template(con) = if not isHtml {con} else [
    #show raw.where(block: false): r => html_elem("code", attrs: (class: "language-"+r.lang), r.text);
    #show raw.where(block: true): r => html_elem("div", attrs: (class: "code-block"))[
        #let lang = r.lang;
        #if lang == none {
            lang = "";
        };
        #html_elem("div", attrs: (class: "code-header"))[
            #html_elem("span", attrs: (class: "language-badge"))[
                #html_elem("i", attrs: (class: "fab mr-1 fa-"+languageIcons.at(lang, default: lang)))[]
                #lang
            ]
            #html_elem("button", attrs: (class: "copy-btn"))[
                #html_elem("i", attrs: (class: "far fa-copy"))[]
                复制代码
            ]
        ]
        #html_elem("div", attrs: (class: "code-content"), html_elem("pre", html_elem("code", attrs: (class: "language-"+lang), r.text)));
    ];
    #html_elem("link", attrs: (href: "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css", rel: "stylesheet"))[];
    #html_elem("link", attrs: (href: "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/styles/atom-one-light.min.css", rel: "stylesheet"))[];
    #html_elem("script", attrs: (src: "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"))[];
    #importStyle("code_block.css", "lib/");
    #importScript("code_block.js", "lib/");
    #con;
];