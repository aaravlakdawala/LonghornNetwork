#!/usr/bin/env python3
"""
generate_uml_comprehensive.py

Generate a comprehensive UML class diagram with:
 - All classes as separate boxes
 - All fields/variables with types and access modifiers
 - All methods with full signatures
 - Inheritance relationships (is-a)
 - Interface implementations (implements)
 - Composition/association relationships (has-a)

Outputs:
 - `uml_comprehensive.dot`  : DOT source
 - `uml_comprehensive.png`  : PNG rendered by Graphviz
"""
import os
import re
import subprocess

REPO_ROOT = os.path.dirname(os.path.abspath(__file__))
SRC_DIR = os.path.join(REPO_ROOT, 'src')
OUT_DOT = os.path.join(REPO_ROOT, 'uml_comprehensive.dot')
OUT_PNG = os.path.join(REPO_ROOT, 'uml_comprehensive.png')

# Regex patterns
class_pattern = re.compile(
    r"\b(class|interface|enum)\s+(\w+)(?:\s+extends\s+([\w<>., ]+))?(?:\s+implements\s+([\w<>., ]+))?", re.M)
field_pattern = re.compile(
    r"^\s*(private|protected|public)?\s+(static\s+)?(final\s+)?(\w+(?:<[^>]*>)?(?:\[\])?)\s+(\w+)\s*(?:[=;]|{)",
    re.MULTILINE)
method_pattern = re.compile(
    r"^\s*(public|private|protected)?\s+(static\s+)?(abstract\s+)?([\w<>\[\], ?]+)\s+(\w+)\s*\(([^)]*)\)",
    re.MULTILINE)


def extract_base_type(typename):
    """Extract base type from generic/array notation."""
    if not typename:
        return ''
    typename = typename.strip()
    typename = re.sub(r"<.*?>", "", typename)
    typename = typename.replace('[]', '')
    typename = typename.split()[-1].split(',')[0]
    return typename


def escape_dot(s):
    """Escape for DOT label."""
    s = str(s).replace('\\', '\\\\').replace(
        '"', '\\"').replace('{', '\\{').replace('}', '\\}')
    s = s.replace('|', '\\|').replace('<', '\\<').replace('>', '\\>')
    return s


# Discover Java files
java_files = []
for root, dirs, files in os.walk(SRC_DIR):
    for f in files:
        if f.endswith('.java'):
            java_files.append(os.path.join(root, f))

classes = {}

# Parse all Java files
for path in java_files:
    content = open(path, 'r', encoding='utf-8').read()

    # Find class definition
    class_match = class_pattern.search(content)
    if not class_match:
        continue

    ctype = class_match.group(1)  # class, interface, enum
    cname = class_match.group(2)
    extends_str = class_match.group(3) or ''
    implements_str = class_match.group(4) or ''

    # Parse extends
    extends_list = [extract_base_type(x) for x in re.split(
        r'[,\s]+', extends_str) if x.strip()]

    # Parse implements
    implements_list = [extract_base_type(x) for x in re.split(
        r'[,\s]+', implements_str) if x.strip()]

    # Extract fields
    fields = []
    for fmatch in field_pattern.finditer(content):
        access = fmatch.group(1) or 'package'
        is_static = bool(fmatch.group(2))
        is_final = bool(fmatch.group(3))
        ftype = fmatch.group(4)
        fname = fmatch.group(5)

        # Skip if not a real field
        if ftype in ('if', 'for', 'while', 'switch', 'try', 'return', 'throw'):
            continue

        # UML visibility
        if access == 'public':
            vis = '+'
        elif access == 'private':
            vis = '-'
        elif access == 'protected':
            vis = '#'
        else:
            vis = '~'

        mod = ('*' if is_static else '') + ('/' if is_final else '')
        fields.append(f'{vis}{mod} {fname}: {ftype}')

    # Extract methods
    methods = []
    for mmatch in method_pattern.finditer(content):
        access = mmatch.group(1) or 'package'
        is_static = bool(mmatch.group(2))
        is_abstract = bool(mmatch.group(3))
        rtype = mmatch.group(4)
        mname = mmatch.group(5)
        params_str = mmatch.group(6)

        # Skip constructors
        if mname == cname:
            continue

        # UML visibility
        if access == 'public':
            vis = '+'
        elif access == 'private':
            vis = '-'
        elif access == 'protected':
            vis = '#'
        else:
            vis = '~'

        mod = ('*' if is_static else '') + ('/' if is_abstract else '')

        # Parse parameters
        params = []
        if params_str.strip():
            for p in params_str.split(','):
                p = p.strip()
                if ' ' in p:
                    ptype, pname = p.rsplit(' ', 1)
                    params.append(f'{pname}: {ptype}')
                elif p:
                    params.append(p)

        param_str = ', '.join(params)
        methods.append(f'{vis}{mod} {mname}({param_str}): {rtype}')

    classes[cname] = {
        'type': ctype,
        'extends': extends_list,
        'implements': implements_list,
        'fields': fields,
        'methods': methods,
        'file': os.path.relpath(path, REPO_ROOT)
    }

# Build DOT file
lines = [
    'digraph UML {',
    '  rankdir=TB;',
    '  nodesep=0.8;',
    '  ranksep=1.2;',
    '  node [shape=record, fontname="Arial", fontsize="10", margin="0.2,0.15", style="filled,rounded", color="black", fillcolor="white"];',
    '  edge [fontname="Arial", fontsize="9", labelfontsize="10"];',
    '  bgcolor=white;',
    ''
]

# Add class nodes
for cname, cinfo in sorted(classes.items()):
    # Build label with sections - each in its own line
    class_header = cname
    if cinfo['type'] == 'interface':
        class_header = f'<<interface>> {cname}'
    elif cinfo['type'] == 'enum':
        class_header = f'<<enum>> {cname}'

    # Create separate sections with better spacing
    sections = [class_header]

    # Fields section - one per line
    if cinfo['fields']:
        # Limit to first 10 fields
        field_section = '\\n'.join(cinfo['fields'][:10])
        if len(cinfo['fields']) > 10:
            field_section += f'\\n... and {len(cinfo["fields"]) - 10} more'
        sections.append(field_section)

    # Methods section - one per line
    if cinfo['methods']:
        # Limit to first 8 methods
        method_section = '\\n'.join(cinfo['methods'][:8])
        if len(cinfo['methods']) > 8:
            method_section += f'\\n... and {len(cinfo["methods"]) - 8} more'
        sections.append(method_section)

    # Build record with pipe separators
    label = '{' + '|'.join(sections) + '}'

    # Color and style based on type with depth
    if cinfo['type'] == 'interface':
        color = '#E6F2FF'
        fillcolor = '#CCE5FF'
        linewidth = '2'
    elif cinfo['type'] == 'enum':
        color = '#E6F9FF'
        fillcolor = '#CCF0FF'
        linewidth = '2'
    else:
        color = '#FFFFCC'
        fillcolor = '#FFFFE6'
        linewidth = '2.5'

    lines.append(
        f'  "{cname}" [label="{escape_dot(label)}", fillcolor="{fillcolor}", color="{color}", penwidth={linewidth}];')

lines.append('')

# Add relationships
relationships = []

# Inheritance (is-a) - solid red arrows with hollow triangle
for cname, cinfo in classes.items():
    for parent in cinfo['extends']:
        if parent in classes:
            relationships.append(
                f'  "{cname}" -> "{parent}" [arrowhead=onormal, label=" extends\\n(is-a)", color="#CC0000", penwidth=2.5, fontcolor="#CC0000"];')

# Interface implementation - dashed blue arrows
for cname, cinfo in classes.items():
    for iface in cinfo['implements']:
        if iface in classes:
            relationships.append(
                f'  "{cname}" -> "{iface}" [arrowhead=onormal, style=dashed, label=" implements", color="#0066FF", penwidth=2.5, fontcolor="#0066FF"];')

# Association (has-a) - solid green arrows with filled diamonds
seen_has_a = set()
for cname, cinfo in classes.items():
    for field_sig in cinfo['fields']:
        # Extract type from field signature
        if ':' in field_sig:
            ftype = field_sig.split(':')[-1].strip()
            fbase = extract_base_type(ftype)
            if fbase in classes and fbase != cname:
                key = (cname, fbase)
                if key not in seen_has_a:
                    seen_has_a.add(key)
                    relationships.append(
                        f'  "{cname}" -> "{fbase}" [arrowhead=vee, arrowtail=odot, label=" has-a", color="#009900", penwidth=2.5, fontcolor="#009900"];')

lines.extend(sorted(relationships))
lines.append('}')

# Write DOT file
dot_content = '\n'.join(lines)
open(OUT_DOT, 'w', encoding='utf-8').write(dot_content)
print(f'Wrote: {OUT_DOT}')

# Render PNG
try:
    subprocess.run(['dot', '-V'], stdout=subprocess.DEVNULL,
                   stderr=subprocess.DEVNULL)
    rc = subprocess.call(['dot', '-Tpng', OUT_DOT, '-o', OUT_PNG])
    if rc == 0:
        print(f'Rendered: {OUT_PNG}')
    else:
        print(f'dot render failed (exit code {rc})')
except FileNotFoundError:
    print('Graphviz not found; DOT file created but PNG not rendered')
