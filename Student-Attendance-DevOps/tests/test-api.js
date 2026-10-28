const http = require('http');
const { spawn } = require('child_process');
const path = require('path');

const PORT = process.env.TEST_PORT || 3055;
let serverProc;

function request(method, pathUrl, body = null) {
  return new Promise((resolve, reject) => {
    const dataString = body ? JSON.stringify(body) : null;
    const req = http.request({
      hostname: '127.0.0.1',
      port: PORT,
      path: pathUrl,
      method: method,
      headers: {
        'Content-Type': 'application/json',
        ...(dataString ? { 'Content-Length': Buffer.byteLength(dataString) } : {})
      }
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          resolve({ status: res.statusCode, data: JSON.parse(data) });
        } catch {
          resolve({ status: res.statusCode, raw: data });
        }
      });
    });
    req.on('error', reject);
    if (dataString) req.write(dataString);
    req.end();
  });
}

async function runTests() {
  console.log('--- Starting Backend API Test Suite ---');
  
  // 1. Start server with PORT=3055
  serverProc = spawn('node', [path.join(__dirname, '../server.js')], {
    env: { ...process.env, PORT: PORT },
    stdio: 'inherit'
  });

  // Wait 2s for server
  await new Promise(r => setTimeout(r, 2000));

  try {
    // Test 1: GET /api/dashboard
    console.log('1. Testing GET /api/dashboard...');
    let res = await request('GET', '/api/dashboard');
    if (res.status !== 200 || !res.data.today_date) throw new Error('Dashboard API failed');
    console.log('   ✔ Dashboard OK:', res.data.total_students, 'students');

    // Test 2: GET /api/students
    console.log('2. Testing GET /api/students...');
    res = await request('GET', '/api/students');
    if (res.status !== 200 || !Array.isArray(res.data)) throw new Error('Students GET failed');
    console.log('   ✔ Students list OK, count:', res.data.length);

    // Test 3: POST /api/students (Add new student)
    console.log('3. Testing POST /api/students...');
    const testRoll = 'TEST999';
    res = await request('POST', '/api/students', {
      roll_no: testRoll,
      name: 'Integration Test User',
      department: 'CSE (AI & ML)'
    });
    if (res.status !== 201 || !res.data.id) throw new Error('Add student failed');
    const newStudentId = res.data.id;
    console.log('   ✔ Student added, ID:', newStudentId);

    // Test 4: Duplicate roll check
    console.log('4. Testing Duplicate Roll Prevention...');
    res = await request('POST', '/api/students', {
      roll_no: testRoll,
      name: 'Duplicate Student',
      department: 'CSE'
    });
    if (res.status !== 409) throw new Error('Duplicate student should return 409, got: ' + res.status);
    console.log('   ✔ Duplicate roll prevented (409 Conflict)');

    // Test 5: PUT /api/students/:id
    console.log('5. Testing PUT /api/students/:id...');
    res = await request('PUT', `/api/students/${newStudentId}`, {
      roll_no: testRoll,
      name: 'Updated Test User',
      department: 'CSE'
    });
    if (res.status !== 200 || res.data.name !== 'Updated Test User') throw new Error('Student update failed');
    console.log('   ✔ Student updated successfully');

    // Test 6: POST /api/attendance (Mark attendance for date)
    console.log('6. Testing POST /api/attendance...');
    const testDate = '2026-10-24';
    const records = [];
    for (let p = 1; p <= 7; p++) {
      records.push({
        student_id: newStudentId,
        period: p,
        status: p <= 5 ? 'Present' : 'Absent'
      });
    }
    res = await request('POST', '/api/attendance', { date: testDate, records });
    if (res.status !== 200 || !res.data.success) throw new Error('Save attendance failed');
    console.log('   ✔ Attendance saved for 7 periods');

    // Test 7: GET /api/attendance/:date
    console.log('7. Testing GET /api/attendance/:date...');
    res = await request('GET', `/api/attendance/${testDate}`);
    if (res.status !== 200 || res.data.count < 7) throw new Error('Get date attendance failed');
    console.log('   ✔ Date attendance fetched OK, count:', res.data.count);

    // Test 8: GET /api/report/monthly
    console.log('8. Testing GET /api/report/monthly...');
    res = await request('GET', '/api/report/monthly?month=10&year=2026&threshold=75');
    if (res.status !== 200 || !res.data.students) throw new Error('Monthly report failed');
    console.log('   ✔ Monthly report OK, total working periods:', res.data.total_working_periods);

    // Test 9: GET /api/report/student/:id
    console.log('9. Testing GET /api/report/student/:id...');
    res = await request('GET', `/api/report/student/${newStudentId}?month=10&year=2026`);
    if (res.status !== 200 || res.data.total_working_periods < 7) throw new Error('Student report failed');
    console.log('   ✔ Student report OK, attendance %:', res.data.attendance_percentage);

    // Test 10: DELETE /api/students/:id
    console.log('10. Testing DELETE /api/students/:id...');
    res = await request('DELETE', `/api/students/${newStudentId}`);
    if (res.status !== 200) throw new Error('Delete student failed');
    console.log('   ✔ Student deleted successfully');

    console.log('\n======================================================');
    console.log(' ALL 10 BACKEND REST API ENDPOINTS PASSED WITH 100% SUCCESS!');
    console.log('======================================================\n');
  } catch (err) {
    console.error('\n❌ Test failed:', err);
    process.exitCode = 1;
  } finally {
    if (serverProc) serverProc.kill();
  }
}

runTests();
