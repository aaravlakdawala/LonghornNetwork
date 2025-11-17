#!/usr/bin/env python3
"""
generate_uml_graphviz.py

Parse Java sources in `src/` and produce a DETAILED UML class diagram using
Graphviz DOT format. Captures:
 - All fields with access modifiers and types
 - All methods with full signatures (access modifiers, return types, parameters)
 - Inheritance (extends) and interface implementation (implements)
 - Associations (field types, method return types, method parameter types)

Outputs:
 - `uml_graphviz.dot`  : DOT source
 - `uml_graphviz.png`  : PNG rendered by Graphviz (if available)

Usage: python generate_uml_graphviz.py
"""
import os
import re
import subprocess
import sys

REPO_ROOT = os.path.dirname(os.path.abspath(__file__))
SRC_DIR = os.path.join(REPO_ROOT, 'src')
OUT_DOT = os.path.join(REPO_ROOT, 'uml_graphviz.dot')
OUT_PNG = os.path.join(REPO_ROOT, 'uml_graphviz.png')

class_pattern = re.compile(
    r"\b(class|interface|enum)\s+(\w+)(?:\s+extends\s+([\w<>., ]+))?(?:\s+implements\s+([\w<>., ]+))?", re.M)

# Capture access modifier, type, and field name
field_pattern = re.compile(
    r"\b((?:private|protected|public|static|final)\s+)*(\w+(?:<[^>]*>)?(?:\[\])?)\s+(\w+)\s*(?:=|;)")

# Capture access modifier, return type, method name, and parameters
method_pattern = re.compile(
    r"\b((?:private|protected|public|static|abstract|synchronized|final)\s+)*(\w+(?:<[^>]*>)?(?:\[\])?)\s+(\w+)\s*\(([^)]*)\)\s*(?:throws\s+[^{]+)?\s*\{")


def extract_simple_type(token):
    """Extract the base type name (remove generics, arrays, whitespace)."""
    if not token:
        return ''
    token = token.strip()
    token = re.sub(r"<.*?>", "", token)
    token = token.replace('[]', '')
    token = token.split()[-1] if token else token
    token = token.split(',')[0]
    return token


def escape_dot_label(s):
    """Escape special characters for DOT format."""
    s = s.replace('\\', '\\\\')
    s = s.replace('"', '\\"')
    s = s.replace('{', '\\{')
    s = s.replace('}', '\\}')
    s = s.replace('|', '\\|')
    s = s.replace('<', '\\<')
    s = s.replace('>', '\\>')
    return s


java_files = []
for root, dirs, files in os.walk(SRC_DIR):
    for f in files:
        if f.endswith('.java'):
            java_files.append(os.path.join(root, f))

classes = {}

# First pass: extract class declarations and their hierarchy
for path in java_files:
    text = open(path, 'r', encoding='utf-8').read()
    m = class_pattern.search(text)
    if not m:
        continue
    kind = m.group(1)
    name = m.group(2)
    extends = m.group(3)
    implements = m.group(4)
    classes[name] = {
        'kind': kind,
        'file': os.path.relpath(path, REPO_ROOT),
        'extends': [extract_simple_type(x) for x in re.split(r'[,\s]+', extends)] if extends else [],
        'implements': [extract_simple_type(x) for x in re.split(r'[,\s]+', implements)] if implements else [],
        'fields': [],
        'methods': [],
        'text': text  # keep full text for detailed parsing
    }

names = set(classes.keys())
associations = []

# Second pass: extract fields and methods with full details
for name, meta in classes.items():
    text = meta['text']

    # Extract fields with access modifiers
    for fm in field_pattern.finditer(text):
        modifier = (fm.group(1) or '').strip() or 'package'
        field_type = fm.group(2)
        field_name = fm.group(3)

        # Skip if this looks like part of a method or annotation
        if field_type in ('if', 'for', 'while', 'switch', 'try', 'catch', 'new', 'return', 'throws'):
            continue

        # Determine visibility symbol for UML
        vis = '~'  # default package
        if 'public' in modifier:
            vis = '+'
        elif 'private' in modifier:
            vis = '-'
        elif 'protected' in modifier:
            vis = '#'

        is_static = 'static' in modifier
        sig = ('%s%s %s: %s' %
               (vis, '*' if is_static else '', field_name, field_type))
        meta['fields'].append({
            'name': field_name,
            'type': field_type,
            'modifier': modifier,
            'signature': sig
        })

        # Record association if field type is another class
        simple_type = extract_simple_type(field_type)
        if simple_type in names and simple_type != name:
            associations.append((name, simple_type, 'field', field_name))

    # Extract methods with full signatures
    for mm in method_pattern.finditer(text):
        modifier = (mm.group(1) or '').strip() or 'package'
        ret_type = mm.group(2)
        method_name = mm.group(3)
        params_str = mm.group(4)

        # Skip constructor if it looks like a class name
        if method_name == name:
            continue

        # Determine visibility symbol
        vis = '~'
        if 'public' in modifier:
            vis = '+'
        elif 'private' in modifier:
            vis = '-'
        elif 'protected' in modifier:
            vis = '#'

        is_abstract = 'abstract' in modifier
        is_static = 'static' in modifier

        # Parse parameters
        parsed_params = []
        if params_str.strip():
            for p in params_str.split(','):
                p = p.strip()
                if ' ' in p:
                    parts = p.rsplit(' ', 1)
                    ptype = parts[0].strip()
                    pname = parts[1].strip()
                else:
                    ptype = p
                    pname = 'arg'
                parsed_params.append((pname, ptype))

        # Build method signature
        param_str = ', '.join(['%s: %s' % (pn, pt)
                              for pn, pt in parsed_params])
        sig = '%s%s%s %s(%s): %s' % (
            vis,
            '*' if is_static else '',
            '/' if is_abstract else '',
            method_name,
            param_str,
            ret_type
        )

        meta['methods'].append({
            'name': method_name,
            'return_type': ret_type,
            'params': parsed_params,
            'modifier': modifier,
            'signature': sig
        })

        # Record associations from return type and parameters
        ret_simple = extract_simple_type(ret_type)
        if ret_simple in names and ret_simple != name:
            associations.append((name, ret_simple, 'return', method_name))

        for pname, ptype in parsed_params:
            p_simple = extract_simple_type(ptype)
            if p_simple in names and p_simple != name:
                associations.append((name, p_simple, 'param', method_name))

dot_lines = [
    'digraph UML {',
    '  rankdir=TB;',
    '  node [shape=record, fontname="Arial", fontsize="10"];',
    '  edge [fontname="Arial", fontsize="9"];',
    '  graph [bgcolor=white];',
    ''
]

# Build node definitions with full UML format using DOT records
for cname, meta in classes.items():
    # Build class header with stereotype and name
    if meta['kind'] == 'interface':
        class_header = '<<interface>>\\n' + cname
    else:
        class_header = cname

    # Build the record: classname | fields | methods
    sections = ['{' + class_header + '}']

    # Add fields section
    if meta['fields']:
        field_lines = []
        for f in meta['fields']:
            field_lines.append(escape_dot_label(f['signature']))
        sections.append('{' + '\\n'.join(field_lines) + '}')

    # Add methods section (all methods, very clearly labeled)
    if meta['methods']:
        method_lines = []
        for m in meta['methods']:
            method_lines.append(escape_dot_label(m['signature']))
        sections.append('{Methods:\\n' + '\\n'.join(method_lines) + '}')

    # Build the full record label with clear sections
    record_label = '{' + '|'.join(sections) + '}'

    # Determine edge color based on type
    edge_color = 'black'
    if meta['kind'] == 'interface':
        edge_color = 'blue'

    dot_lines.append('  "%s" [label="%s", color="%s", fontsize="11", fontname="Arial"];' %
                     (cname, record_label, edge_color))

dot_lines.append('')

# Add inheritance edges (extends) - labeled "is-a"
for cname, meta in classes.items():
    for parent in meta['extends']:
        if parent and parent in names:
            dot_lines.append(
                '  "%s" -> "%s" [arrowhead=onormal, label=" is-a (extends)", style=solid, color=darkred, penwidth=2.5];' % (cname, parent))

# Add interface implementation edges (implements) - labeled "implements"
for cname, meta in classes.items():
    for iface in meta['implements']:
        if iface and iface in names:
            dot_lines.append(
                '  "%s" -> "%s" [arrowhead=onormal, style=dashed, label=" implements", color=blue, penwidth=2];' % (cname, iface))

# Add composition/association edges (has-a) - only for field-based associations
seen_has_a = set()
for src, dst, kind, detail in associations:
    if kind == 'field':
        key = (src, dst)
        if key not in seen_has_a:
            seen_has_a.add(key)
            dot_lines.append(
                '  "%s" -> "%s" [arrowhead=vee, label=" has-a (%s)", style=solid, color=darkgreen, penwidth=1.5];' % (src, dst, detail))

# Add uses/dependency edges (for method parameters and returns)
seen_uses = set()
for src, dst, kind, detail in associations:
    if kind in ('param', 'return'):
        key = (src, dst, kind)
        if key not in seen_uses:
            seen_uses.add(key)
            label_text = 'uses' if kind == 'param' else 'returns'
            dot_lines.append(
                '  "%s" -> "%s" [arrowhead=open, label=" %s", style=dotted, color=gray50];' % (src, dst, label_text))

dot_lines.append('}')

open(OUT_DOT, 'w', encoding='utf-8').write('\n'.join(dot_lines))
print('Wrote DOT:', OUT_DOT)

# Try to render PNG using dot if available
try:
    subprocess.run(['dot', '-V'], stdout=subprocess.DEVNULL,
                   stderr=subprocess.DEVNULL)
    rc = subprocess.call(['dot', '-Tpng', OUT_DOT, '-o', OUT_PNG])
    if rc == 0:
        print('Rendered PNG:', OUT_PNG)
    else:
        print('dot failed with exit code', rc)
except FileNotFoundError:
    print('Graphviz `dot` not found. DOT file produced but PNG not rendered.')
