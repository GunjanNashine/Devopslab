const http = require('http');
const fs = require('fs');
const path = require('path');
const os = require('os');
const url = require('url');

const PORT = process.env.PORT || 3000;
const PUBLIC_DIR = path.join(__dirname, 'public');

/**
 * Palindrome evaluation logic
 */
function evaluatePalindrome(input) {
    if (typeof input !== 'string') input = '';
    
    // Clean string: remove non-alphanumeric, lowercase
    const cleaned = input.replace(/[^a-zA-Z0-9]/g, '').toLowerCase();
    const reversedCleaned = cleaned.split('').reverse().join('');
    const isPal = cleaned.length > 0 && cleaned === reversedCleaned;
    const isStrict = input.length > 0 && input === input.split('').reverse().join('');

    return {
        original: input,
        cleaned: cleaned,
        reversed: reversedCleaned,
        isPalindrome: isPal,
        isStrictPalindrome: isStrict,
        charCountOriginal: input.length,
        charCountCleaned: cleaned.length,
        evaluatedAt: new Date().toISOString()
    };
}

const server = http.createServer((req, res) => {
    const parsedUrl = url.parse(req.url, true);
    const pathname = parsedUrl.pathname;

    // CORS & Common Headers
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

    if (req.method === 'OPTIONS') {
        res.writeHead(204);
        return res.end();
    }

    // Kubernetes Health Probes (Liveness & Readiness)
    if (pathname === '/health') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        return res.end(JSON.stringify({
            status: 'UP',
            service: 'palindrome-k8s-app',
            podName: os.hostname(),
            platform: os.platform(),
            nodeVersion: process.version,
            uptimeSeconds: Math.floor(process.uptime()),
            memoryUsageMB: Math.round(process.memoryUsage().heapUsed / 1024 / 1024),
            timestamp: new Date().toISOString()
        }));
    }

    // API Endpoint: /api/check (Supports GET and POST)
    if (pathname === '/api/check') {
        if (req.method === 'GET') {
            const str = parsedUrl.query.str || '';
            const result = evaluatePalindrome(str);
            res.writeHead(200, { 'Content-Type': 'application/json' });
            return res.end(JSON.stringify(result));
        } else if (req.method === 'POST') {
            let body = '';
            req.on('data', chunk => { body += chunk; });
            req.on('end', () => {
                let str = '';
                try {
                    const data = JSON.parse(body);
                    str = data.str || data.input || '';
                } catch (e) {
                    str = body;
                }
                const result = evaluatePalindrome(str);
                res.writeHead(200, { 'Content-Type': 'application/json' });
                return res.end(JSON.stringify(result));
            });
            return;
        }
    }

    // Static File Serving
    let filePath = path.join(PUBLIC_DIR, pathname === '/' ? 'index.html' : pathname);

    const extname = path.extname(filePath).toLowerCase();
    const mimeTypes = {
        '.html': 'text/html; charset=utf-8',
        '.css': 'text/css; charset=utf-8',
        '.js': 'application/javascript; charset=utf-8',
        '.json': 'application/json; charset=utf-8',
        '.png': 'image/png',
        '.jpg': 'image/jpeg',
        '.svg': 'image/svg+xml',
        '.ico': 'image/x-icon'
    };

    const contentType = mimeTypes[extname] || 'application/octet-stream';

    fs.readFile(filePath, (err, content) => {
        if (err) {
            if (err.code === 'ENOENT') {
                res.writeHead(404, { 'Content-Type': 'text/html; charset=utf-8' });
                res.end('<h1>404 Not Found</h1><p>The requested file does not exist.</p>');
            } else {
                res.writeHead(500);
                res.end(`Server Error: ${err.code}`);
            }
        } else {
            res.writeHead(200, { 'Content-Type': contentType });
            res.end(content, 'utf-8');
        }
    });
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`====================================================`);
    console.log(` 🚀 Palindrome K8s Application Server Running!`);
    console.log(` 🌐 Local Access : http://localhost:${PORT}`);
    console.log(` 🩺 Health Probe : http://localhost:${PORT}/health`);
    console.log(` 📦 Pod Hostname : ${os.hostname()}`);
    console.log(`====================================================`);
});

// Graceful shutdown for Kubernetes SIGTERM
process.on('SIGTERM', () => {
    console.log('Received SIGTERM signal. Closing HTTP server gracefully...');
    server.close(() => {
        console.log('HTTP server closed. Exiting process.');
        process.exit(0);
    });
});
