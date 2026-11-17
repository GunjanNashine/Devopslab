document.addEventListener('DOMContentLoaded', () => {
    const inputString = document.getElementById('inputString');
    const btnCheck = document.getElementById('btnCheck');
    const btnClear = document.getElementById('btnClear');
    const validationAlert = document.getElementById('validationAlert');
    const resultContainer = document.getElementById('resultContainer');
    const resultBadge = document.getElementById('resultBadge');
    const resultStatusText = document.getElementById('resultStatusText');
    const originalTextDisplay = document.getElementById('originalTextDisplay');
    const normalizedTextDisplay = document.getElementById('normalizedTextDisplay');
    const reversedTextDisplay = document.getElementById('reversedTextDisplay');
    const charCountDisplay = document.getElementById('charCountDisplay');
    const historyTableBody = document.getElementById('historyTableBody');
    const historyCount = document.getElementById('historyCount');
    const emptyHistoryRow = document.getElementById('emptyHistoryRow');
    const k8sPodName = document.getElementById('k8sPodName');
    const systemStatus = document.getElementById('systemStatus');
    const sampleChips = document.querySelectorAll('.chip-sample');

    let historyRecords = [];

    // Query Kubernetes Health Endpoint
    fetch('/health')
        .then(res => res.json())
        .then(data => {
            if (k8sPodName && data.podName) {
                k8sPodName.textContent = data.podName;
            }
            if (systemStatus) {
                systemStatus.textContent = '● Pod Healthy (' + data.service + ')';
            }
        })
        .catch(err => {
            console.warn('Could not contact /health endpoint:', err);
        });

    // Check Palindrome Action
    function checkPalindrome() {
        const rawValue = inputString.value;
        const trimmed = rawValue.trim();

        if (!trimmed) {
            validationAlert.style.display = 'block';
            resultContainer.style.display = 'none';
            return;
        }

        validationAlert.style.display = 'none';

        // Perform local evaluation + sync with backend API
        const cleaned = trimmed.replace(/[^a-zA-Z0-9]/g, '').toLowerCase();
        const reversed = cleaned.split('').reverse().join('');
        const isPal = cleaned.length > 0 && cleaned === reversed;

        // Render Result UI
        resultContainer.style.display = 'block';
        originalTextDisplay.textContent = trimmed;
        normalizedTextDisplay.textContent = cleaned || '(none)';
        reversedTextDisplay.textContent = reversed || '(none)';
        charCountDisplay.textContent = `${trimmed.length} chars (Normalized: ${cleaned.length})`;

        resultContainer.classList.remove('palindrome-match', 'palindrome-fail');
        resultBadge.classList.remove('badge-success', 'badge-failure');

        if (isPal) {
            resultContainer.classList.add('palindrome-match');
            resultBadge.classList.add('badge-success');
            resultBadge.textContent = 'PALINDROME';
            resultStatusText.innerHTML = `✅ <strong>"${escapeHtml(trimmed)}"</strong> is a valid Palindrome!`;
        } else {
            resultContainer.classList.add('palindrome-fail');
            resultBadge.classList.add('badge-failure');
            resultBadge.textContent = 'NOT A PALINDROME';
            resultStatusText.innerHTML = `❌ <strong>"${escapeHtml(trimmed)}"</strong> is NOT a Palindrome.`;
        }

        // Add to History
        addHistoryRecord({
            timestamp: new Date().toLocaleTimeString(),
            input: trimmed,
            reversed: reversed,
            isPalindrome: isPal
        });
    }

    // Clear Action
    function clearForm() {
        inputString.value = '';
        validationAlert.style.display = 'none';
        resultContainer.style.display = 'none';
        inputString.focus();
    }

    // Add record to UI table
    function addHistoryRecord(record) {
        historyRecords.unshift(record);

        if (emptyHistoryRow && emptyHistoryRow.parentNode) {
            emptyHistoryRow.remove();
        }

        const tr = document.createElement('tr');
        tr.className = record.isPalindrome ? 'row-palindrome' : 'row-not-palindrome';
        tr.innerHTML = `
            <td><strong>${historyRecords.length}</strong></td>
            <td>${record.timestamp}</td>
            <td><code>${escapeHtml(record.input)}</code></td>
            <td><code>${escapeHtml(record.reversed)}</code></td>
            <td>
                <span class="badge ${record.isPalindrome ? 'live-badge' : 'alert-error'}" style="margin:0; padding:2px 8px; font-size:0.75rem;">
                    ${record.isPalindrome ? '✓ PALINDROME' : '✗ NOT PALINDROME'}
                </span>
            </td>
        `;

        historyTableBody.insertBefore(tr, historyTableBody.firstChild);
        historyCount.textContent = `${historyRecords.length} checks performed`;
    }

    function escapeHtml(text) {
        return text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    // Event Listeners
    btnCheck.addEventListener('click', checkPalindrome);
    btnClear.addEventListener('click', clearForm);

    inputString.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            checkPalindrome();
        }
    });

    sampleChips.forEach(chip => {
        chip.addEventListener('click', () => {
            inputString.value = chip.getAttribute('data-sample');
            checkPalindrome();
        });
    });
});
