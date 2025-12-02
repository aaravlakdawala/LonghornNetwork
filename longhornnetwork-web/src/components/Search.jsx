import React, { useState, useMemo } from 'react';

export default function Search({ executorData }) {
  const [searchQuery, setSearchQuery] = useState('');

  // Extract nodes from executorData
  const nodes = useMemo(() => {
    if (!executorData || !executorData.graph || !executorData.graph.nodes) return [];
    return executorData.graph.nodes;
  }, [executorData]);

  // Filter students by name or internship
  const filteredStudents = useMemo(() => {
    if (!searchQuery.trim() || !nodes) return [];
    const query = searchQuery.toLowerCase();
    return nodes.filter(node => {
      const nameMatch = node.name && node.name.toLowerCase().includes(query);
      const internshipMatch = node.previousInternships && node.previousInternships.some(intern => intern.toLowerCase().includes(query));
      return nameMatch || internshipMatch;
    });
  }, [searchQuery, nodes]);

  return (
    <div style={{ marginTop: '80px', padding: '20px' }}>
      <h2 style={{ marginTop: 0, color: '#333' }}>🔍 Search Students</h2>
      
      <div style={{
        backgroundColor: '#f9fafb',
        padding: '15px',
        borderRadius: '5px',
        border: '1px solid #e5e7eb',
        marginBottom: '20px'
      }}>
        <input
          type="text"
          placeholder="Search by name or internship (e.g., 'Google', 'Alice')"
          value={searchQuery}
          onChange={e => setSearchQuery(e.target.value)}
          style={{
            width: '100%',
            padding: '12px',
            fontSize: '14px',
            border: '1px solid #d1d5db',
            borderRadius: '5px',
            boxSizing: 'border-box'
          }}
        />
      </div>

      {searchQuery && (
        <div>
          <p style={{ color: '#666', fontSize: '14px', marginBottom: '16px' }}>
            Found <strong>{filteredStudents.length}</strong> student{filteredStudents.length !== 1 ? 's' : ''}
          </p>
          
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))', gap: '16px' }}>
            {filteredStudents.length > 0 ? (
              filteredStudents.map((student) => (
                <div
                  key={student.id}
                  style={{
                    backgroundColor: '#fff',
                    padding: '16px',
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px',
                    boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
                    color: '#000'
                  }}
                >
                  <h3 style={{ margin: '0 0 12px 0', fontSize: '16px', color: '#06b6d4' }}>
                    {student.name}
                  </h3>

                  {/* Basic Info */}
                  <div style={{ marginBottom: '12px', paddingBottom: '12px', borderBottom: '1px solid #e5e7eb' }}>
                    <p style={{ margin: '4px 0' }}><strong>Age:</strong> {student.age}</p>
                    <p style={{ margin: '4px 0' }}><strong>Gender:</strong> {student.gender}</p>
                    <p style={{ margin: '4px 0' }}><strong>Year:</strong> {student.year}</p>
                    <p style={{ margin: '4px 0' }}><strong>Major:</strong> {student.major}</p>
                    <p style={{ margin: '4px 0' }}><strong>GPA:</strong> {student.gpa?.toFixed(2)}</p>
                  </div>

                  {/* Internships */}
                  {student.previousInternships && student.previousInternships.length > 0 && (
                    <div style={{ marginBottom: '12px', paddingBottom: '12px', borderBottom: '1px solid #e5e7eb' }}>
                      <h4 style={{ margin: '0 0 8px 0', fontSize: '12px', color: '#333' }}>💼 Internships</h4>
                      <ul style={{ margin: '0', paddingLeft: '18px', color: '#000' }}>
                        {student.previousInternships.map((intern, idx) => (
                          <li key={idx} style={{ fontSize: '13px', marginBottom: '4px' }}>{intern}</li>
                        ))}
                      </ul>
                    </div>
                  )}

                  {/* Friends */}
                  {student.friends && student.friends.length > 0 && (
                    <div style={{ marginBottom: '12px', paddingBottom: '12px', borderBottom: '1px solid #e5e7eb' }}>
                      <h4 style={{ margin: '0 0 8px 0', fontSize: '12px', color: '#333' }}>🤝 Friends</h4>
                      <ul style={{ margin: '0', paddingLeft: '18px', color: '#000' }}>
                        {student.friends.map((friend, idx) => (
                          <li key={idx} style={{ fontSize: '13px', marginBottom: '4px' }}>{friend}</li>
                        ))}
                      </ul>
                    </div>
                  )}

                  {/* Roommate Preferences */}
                  {student.roommatePreferences && student.roommatePreferences.length > 0 && (
                    <div style={{ marginBottom: '12px', paddingBottom: '12px', borderBottom: '1px solid #e5e7eb' }}>
                      <h4 style={{ margin: '0 0 8px 0', fontSize: '12px', color: '#333' }}>❤️ Roommate Preferences</h4>
                      <ul style={{ margin: '0', paddingLeft: '18px', color: '#000' }}>
                        {student.roommatePreferences.map((pref, idx) => (
                          <li key={idx} style={{ fontSize: '13px', marginBottom: '4px' }}>{pref}</li>
                        ))}
                      </ul>
                    </div>
                  )}

                  {/* Roommate Info */}
                  <div>
                    <h4 style={{ margin: '0 0 8px 0', fontSize: '12px', color: '#333' }}>🛏️ Roommate</h4>
                    <p style={{ margin: '0', fontSize: '13px', color: '#000' }}>
                      {student.roommate ? student.roommate : <span style={{ fontStyle: 'italic', color: '#999' }}>Not assigned</span>}
                    </p>
                  </div>
                </div>
              ))
            ) : (
              <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px', color: '#999' }}>
                <p style={{ fontSize: '16px' }}>No students found matching "{searchQuery}"</p>
              </div>
            )}
          </div>
        </div>
      )}

      {!searchQuery && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
          <p style={{ fontSize: '16px' }}>Enter a search query to find students by name or internship</p>
        </div>
      )}
    </div>
  );
}
