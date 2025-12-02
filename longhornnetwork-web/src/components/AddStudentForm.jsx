import React, { useState } from 'react';

function AddStudentForm({ onStudentAdded }) {
  const [formData, setFormData] = useState({
    name: '',
    age: '',
    gender: 'Male',
    year: '1',
    major: '',
    gpa: '',
    roommatePreferences: '',
    previousInternships: ''
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      // Convert preferences and internships to arrays
      const preferences = formData.roommatePreferences
        .split(',')
        .map(p => p.trim())
        .filter(p => p.length > 0);

      const internships = formData.previousInternships
        .split(',')
        .map(i => i.trim())
        .filter(i => i.length > 0);

      const payload = {
        name: formData.name,
        age: parseInt(formData.age),
        gender: formData.gender,
        year: parseInt(formData.year),
        major: formData.major,
        gpa: parseFloat(formData.gpa),
        roommatePreferences: preferences,
        previousInternships: internships
      };

      const response = await fetch('http://localhost:8080/api/student', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      const data = await response.json();

      if (response.ok) {
        setMessage(`✅ Student "${formData.name}" added successfully!`);
        // Reset form
        setFormData({
          name: '',
          age: '',
          gender: 'Male',
          year: '1',
          major: '',
          gpa: '',
          roommatePreferences: '',
          previousInternships: ''
        });
        // Notify parent component
        if (onStudentAdded) {
          onStudentAdded(data.graph);
        }
        // Clear message after 3 seconds
        setTimeout(() => setMessage(''), 3000);
      } else {
        setMessage(`❌ Error: ${data.message}`);
      }
    } catch (error) {
      setMessage(`❌ Failed to add student: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      padding: '20px',
      maxWidth: '500px',
      margin: '80px auto 0',
      backgroundColor: '#f5f5f5',
      borderRadius: '8px',
      boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
    }}>
      <h2 style={{ marginTop: 0, color: '#333', textAlign: 'center' }}>➕ Add New Student</h2>

      {message && (
        <div style={{
          padding: '12px',
          marginBottom: '15px',
          backgroundColor: message.includes('✅') ? '#e8f5e9' : '#ffebee',
          color: message.includes('✅') ? '#2e7d32' : '#c62828',
          borderRadius: '4px',
          fontSize: '14px',
          border: `1px solid ${message.includes('✅') ? '#81c784' : '#e57373'}`
        }}>
          {message}
        </div>
      )}

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {/* Name */}
        <div>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold', color: '#333' }}>
            Name *
          </label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            required
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ddd',
              fontSize: '14px',
              boxSizing: 'border-box'
            }}
            placeholder="e.g., John Doe"
          />
        </div>

        {/* Age */}
        <div>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold', color: '#333' }}>
            Age *
          </label>
          <input
            type="number"
            name="age"
            value={formData.age}
            onChange={handleInputChange}
            required
            min="15"
            max="60"
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ddd',
              fontSize: '14px',
              boxSizing: 'border-box'
            }}
            placeholder="e.g., 20"
          />
        </div>

        {/* Gender */}
        <div>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold', color: '#333' }}>
            Gender *
          </label>
          <select
            name="gender"
            value={formData.gender}
            onChange={handleInputChange}
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ddd',
              fontSize: '14px',
              boxSizing: 'border-box'
            }}
          >
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>
        </div>

        {/* Year */}
        <div>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold', color: '#333' }}>
            Year *
          </label>
          <select
            name="year"
            value={formData.year}
            onChange={handleInputChange}
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ddd',
              fontSize: '14px',
              boxSizing: 'border-box'
            }}
          >
            <option value="1">Freshman</option>
            <option value="2">Sophomore</option>
            <option value="3">Junior</option>
            <option value="4">Senior</option>
          </select>
        </div>

        {/* Major */}
        <div>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold', color: '#333' }}>
            Major *
          </label>
          <input
            type="text"
            name="major"
            value={formData.major}
            onChange={handleInputChange}
            required
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ddd',
              fontSize: '14px',
              boxSizing: 'border-box'
            }}
            placeholder="e.g., Computer Science"
          />
        </div>

        {/* GPA */}
        <div>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold', color: '#333' }}>
            GPA *
          </label>
          <input
            type="number"
            name="gpa"
            value={formData.gpa}
            onChange={handleInputChange}
            required
            min="0"
            max="4"
            step="0.1"
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ddd',
              fontSize: '14px',
              boxSizing: 'border-box'
            }}
            placeholder="e.g., 3.8"
          />
        </div>

        {/* Roommate Preferences */}
        <div>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold', color: '#333' }}>
            Roommate Preferences (comma-separated)
          </label>
          <textarea
            name="roommatePreferences"
            value={formData.roommatePreferences}
            onChange={handleInputChange}
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ddd',
              fontSize: '14px',
              boxSizing: 'border-box',
              fontFamily: 'monospace',
              minHeight: '60px',
              resize: 'vertical'
            }}
            placeholder="e.g., quiet, organized, morning person"
          />
        </div>

        {/* Previous Internships */}
        <div>
          <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold', color: '#333' }}>
            Previous Internships (comma-separated)
          </label>
          <textarea
            name="previousInternships"
            value={formData.previousInternships}
            onChange={handleInputChange}
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ddd',
              fontSize: '14px',
              boxSizing: 'border-box',
              fontFamily: 'monospace',
              minHeight: '60px',
              resize: 'vertical'
            }}
            placeholder="e.g., Google, Amazon, Microsoft"
          />
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          disabled={loading}
          style={{
            padding: '10px',
            backgroundColor: loading ? '#ccc' : '#06b6d4',
            color: '#fff',
            border: 'none',
            borderRadius: '4px',
            fontSize: '16px',
            fontWeight: 'bold',
            cursor: loading ? 'not-allowed' : 'pointer',
            marginTop: '10px',
            transition: 'background-color 0.3s'
          }}
          onMouseEnter={(e) => !loading && (e.target.style.backgroundColor = '#0891b2')}
          onMouseLeave={(e) => !loading && (e.target.style.backgroundColor = '#06b6d4')}
        >
          {loading ? '⏳ Adding...' : '✨ Add Student'}
        </button>
      </form>

      <div style={{
        marginTop: '15px',
        padding: '10px',
        backgroundColor: '#e3f2fd',
        borderRadius: '4px',
        fontSize: '12px',
        color: '#1565c0',
        border: '1px solid #90caf9'
      }}>
        <strong>💡 Tip:</strong> Fields marked with * are required. Preferences and internships can be left blank or separated by commas.
      </div>
    </div>
  );
}

export default AddStudentForm;
