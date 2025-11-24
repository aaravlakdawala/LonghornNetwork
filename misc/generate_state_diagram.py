#!/usr/bin/env python3
"""
generate_state_diagram.py

Generate a detailed state transition diagram for the LonghornNetwork system.
Shows ALL possible state transitions including:
 - Success and failure paths for each test phase
 - Loop iterations (test case processing)
 - Conditional branches (error handling, size checks)
 - Score accumulation across tests

Outputs:
 - `state_diagram.dot`  : DOT source
 - `state_diagram.png`  : PNG rendered by Graphviz
"""
import os
import subprocess

REPO_ROOT = os.path.dirname(os.path.abspath(__file__))
OUT_DOT = os.path.join(REPO_ROOT, 'state_diagram.dot')
OUT_PNG = os.path.join(REPO_ROOT, 'state_diagram.png')

# Define states and transitions with comprehensive coverage
# Tuple format: (id, label, shape, color)
states = [
    ('START', 'START\\nInitialize', 'ellipse',
     '#90EE90'),  # Light green - Start/End
    ('LOAD_TEST_CASES', 'Load Test Cases\\n(TC1, TC2, TC3)',
     'box', '#FFE4B5'),  # Moccasin - Action
    ('LOOP_START', 'For Each\\nTest Case', 'box', '#FFDAB9'),  # Peach - Action
    ('PARSE_DATA', 'Parse & Display\\nStudent Data', 'box', '#FFE4B5'),

    # Graph Phase
    ('BUILD_GRAPH', 'Build\\nStudentGraph', 'box', '#87CEEB'),  # Sky blue - Action
    ('CHECK_RECIPROCAL', 'Reciprocal\\nEdges?',
     'diamond', '#F0E68C'),  # Diamond - Decision
    ('GRAPH_PASS', '✓ Graph Valid\\n(+30 pts)', 'box', '#90EE90'),  # Green - Result
    ('GRAPH_FAIL', '✗ Graph Failed\\n(+0 pts)', 'box', '#FFB6C6'),  # Red - Result

    # GaleShapley Phase
    ('RUN_MATCHING', 'Run GaleShapley\\nMatching',
     'box', '#DDA0DD'),  # Plum - Action
    ('VALIDATE_ROOMMATES', 'Roommate\\nPairs Valid?',
     'diamond', '#F0E68C'),  # Diamond - Decision
    ('MATCH_PASS', '✓ Matching Valid\\n(+20 pts)',
     'box', '#90EE90'),  # Green - Result
    ('MATCH_FAIL', '✗ Matching Failed\\n(+0 pts)', 'box', '#FFB6C6'),  # Red - Result

    # Threading Phase
    ('CHECK_SIZE', 'students\\nsize >= 2?',
     'diamond', '#F0E68C'),  # Diamond - Decision
    ('SIZE_FAIL', '✗ Not Enough\\nStudents (+0 pts)',
     'box', '#FFB6C6'),  # Red - Result
    ('CREATE_EXECUTOR', 'Create Thread\\nPool (4)', 'box', '#DDA0DD'),  # Action
    ('SUBMIT_THREADS', 'Submit 4 Tasks\\n(FriendReq+Chat)', 'box', '#DDA0DD'),  # Action
    ('WAIT_THREADS', 'Wait for Threads\\n(5s timeout)', 'box', '#DDA0DD'),  # Action
    ('THREADS_PASS', '✓ Threads Done\\n(+20 pts)',
     'box', '#90EE90'),  # Green - Result
    ('THREADS_FAIL', '✗ Timeout/Error\\n(+0 pts)', 'box', '#FFB6C6'),  # Red - Result

    # Referral Phase
    ('BUILD_GRAPH2', 'Build Graph\\n(2nd time)', 'box', '#87CEEB'),  # Action
    ('CREATE_FINDER', 'Create\\nReferralPathFinder',
     'box', '#FFB6C1'),  # Light pink - Action
    ('SEARCH_REFERRAL', 'Search for\\nReferral Path', 'box', '#FFB6C1'),  # Action
    ('CHECK_TC2', 'Test Case\\n== 2?', 'diamond', '#F0E68C'),  # Diamond - Decision
    ('REFERRAL_PASS', '✓ Path Found\\n(+10 pts)',
     'box', '#90EE90'),  # Green - Result
    ('REFERRAL_FAIL', '✗ No Path\\n(+0 pts)', 'box', '#FFB6C6'),  # Red - Result

    # Integration Phase
    ('INTEGRATION', 'Integration Tests\\n(+20 pts)',
     'box', '#90EE90'),  # Green - Result

    # Scoring & Loop Control
    ('ADD_SCORE', 'Add Score to\\nTotal', 'box', '#DEB887'),  # Burlywood - Action
    ('CHECK_LOOP', 'i < 3?', 'diamond', '#F0E68C'),  # Diamond - Decision
    ('LOOP_NEXT', 'i++', 'box', '#FFDAB9'),  # Peach - Action
    ('CALC_AVERAGE', 'Calculate\\nAverage Score', 'box', '#DEB887'),  # Action

    ('OUTPUT_FINAL', 'Output Final\\nResults',
     'box', '#D3D3D3'),  # Light gray - Action
    ('END', 'END\\nComplete', 'ellipse', '#90EE90'),  # Light green - Start/End
]

transitions = [
    # Initial setup
    ('START', 'LOAD_TEST_CASES', ''),
    ('LOAD_TEST_CASES', 'LOOP_START', 'i=0'),

    # Main loop for each test case
    ('LOOP_START', 'PARSE_DATA', 'get TC[i]'),

    # ========== PHASE 1: Graph Construction ==========
    ('PARSE_DATA', 'BUILD_GRAPH', ''),
    ('BUILD_GRAPH', 'CHECK_RECIPROCAL', ''),
    ('CHECK_RECIPROCAL', 'GRAPH_PASS', 'YES'),
    ('CHECK_RECIPROCAL', 'GRAPH_FAIL', 'NO'),

    # ========== PHASE 2: GaleShapley Matching ==========
    ('GRAPH_PASS', 'RUN_MATCHING', ''),
    ('GRAPH_FAIL', 'RUN_MATCHING', ''),
    ('RUN_MATCHING', 'VALIDATE_ROOMMATES', ''),
    ('VALIDATE_ROOMMATES', 'MATCH_PASS', 'YES'),
    ('VALIDATE_ROOMMATES', 'MATCH_FAIL', 'NO'),

    # ========== PHASE 3: Threading ==========
    ('MATCH_PASS', 'CHECK_SIZE', ''),
    ('MATCH_FAIL', 'CHECK_SIZE', ''),
    ('CHECK_SIZE', 'CREATE_EXECUTOR', 'YES'),
    ('CHECK_SIZE', 'SIZE_FAIL', 'NO'),
    ('SIZE_FAIL', 'BUILD_GRAPH2', ''),
    ('CREATE_EXECUTOR', 'SUBMIT_THREADS', ''),
    ('SUBMIT_THREADS', 'WAIT_THREADS', ''),
    ('WAIT_THREADS', 'THREADS_PASS', 'YES'),
    ('WAIT_THREADS', 'THREADS_FAIL', 'NO'),
    ('THREADS_PASS', 'BUILD_GRAPH2', ''),
    ('THREADS_FAIL', 'BUILD_GRAPH2', ''),

    # ========== PHASE 4: Referral Path ==========
    ('BUILD_GRAPH2', 'CREATE_FINDER', ''),
    ('CREATE_FINDER', 'SEARCH_REFERRAL', ''),
    ('SEARCH_REFERRAL', 'CHECK_TC2', ''),
    ('CHECK_TC2', 'REFERRAL_PASS', 'YES'),
    ('CHECK_TC2', 'REFERRAL_FAIL', 'NO'),

    # ========== PHASE 5: Integration ==========
    ('REFERRAL_PASS', 'INTEGRATION', ''),
    ('REFERRAL_FAIL', 'INTEGRATION', ''),

    # ========== Scoring & Loop Control ==========
    ('INTEGRATION', 'ADD_SCORE', ''),
    ('ADD_SCORE', 'CHECK_LOOP', ''),
    ('CHECK_LOOP', 'LOOP_NEXT', 'YES'),
    ('CHECK_LOOP', 'CALC_AVERAGE', 'NO'),
    ('LOOP_NEXT', 'LOOP_START', ''),

    # ========== Final Output ==========
    ('CALC_AVERAGE', 'OUTPUT_FINAL', ''),
    ('OUTPUT_FINAL', 'END', ''),
]

# Build DOT file
lines = [
    'digraph STATE_MACHINE {',
    '  rankdir=LR;',
    '  ranksep=0.6;',
    '  nodesep=0.4;',
    '  node [fontname="Arial", fontsize="8", style="filled"];',
    '  edge [fontname="Arial", fontsize="7", labelfontsize="7"];',
    '  bgcolor=white;',
    ''
]

# Add state nodes with different shapes
for state_id, label, shape, color in states:
    if shape == 'diamond':
        # Diamond for decisions
        lines.append(
            f'  "{state_id}" [label="{label}", shape="diamond", fillcolor="{color}", color="black", penwidth=1.5];')
    elif shape == 'ellipse':
        # Ellipse for start/end
        lines.append(
            f'  "{state_id}" [label="{label}", shape="ellipse", fillcolor="{color}", color="black", penwidth=2];')
    else:
        # Box for actions/results (default)
        lines.append(
            f'  "{state_id}" [label="{label}", shape="box", fillcolor="{color}", color="black", penwidth=1.2, style="filled,rounded"];')

lines.append('')

# Add transitions with edge coloring
for src, dst, label in transitions:
    # Color edges based on outcome
    if 'FAIL' in dst:
        edge_color = 'red'
    elif 'PASS' in dst:
        edge_color = 'green'
    elif 'INTEGRATION' in dst or 'ADD_SCORE' in dst:
        edge_color = 'green'
    elif 'NO' in label:
        edge_color = 'red'
    elif 'YES' in label:
        edge_color = 'green'
    else:
        edge_color = 'black'

    if label:
        lines.append(
            f'  "{src}" -> "{dst}" [label=" {label}", color="{edge_color}", penwidth=1.2];')
    else:
        lines.append(
            f'  "{src}" -> "{dst}" [color="{edge_color}", penwidth=1.2];')

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
