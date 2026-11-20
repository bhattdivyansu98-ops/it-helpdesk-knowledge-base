CREATE DATABASE IF NOT EXISTS helpdesk_kb;
USE helpdesk_kb;

CREATE TABLE IF NOT EXISTS articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    keywords VARCHAR(300),
    steps TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_article_category ON articles(category);
CREATE FULLTEXT INDEX ft_article ON articles(title, keywords, steps);

INSERT INTO articles (title, category, keywords, steps) VALUES
('Wi-Fi connected but no internet', 'Network', 'wifi wi-fi internet router no connection dns',
'1. Check that other devices can open websites. If not, the router or ISP is the problem.
2. Turn Wi-Fi off and on again on your computer.
3. Restart the router: unplug for 30 seconds, plug back in, wait 2 minutes.
4. Open Command Prompt and run: ipconfig /release, then ipconfig /renew.
5. Flush DNS: ipconfig /flushdns.
6. Run Windows Network Troubleshooter (Settings > Network & Internet > Status > Network troubleshooter).
7. If still not working, escalate to L2 with the output of ipconfig /all.'),

('Unable to connect to Wi-Fi network', 'Network', 'wifi wireless cannot connect password adapter',
'1. Make sure Airplane mode is OFF and Wi-Fi is turned ON.
2. Click the network name > Forget, then reconnect and re-enter the password carefully.
3. Move closer to the router to rule out weak signal.
4. Device Manager > Network adapters > right-click the Wi-Fi adapter > Disable, then Enable.
5. Restart the computer and try again.'),

('Printer shows offline', 'Printer', 'printer offline not printing queue spooler',
'1. Check the printer is ON and connected (USB cable or same Wi-Fi/LAN).
2. Settings > Printers & scanners > select the printer > make sure "Use printer offline" is unchecked.
3. Clear stuck jobs: open the print queue and cancel all documents.
4. Restart the Print Spooler: press Win+R, type services.msc, find Print Spooler, right-click > Restart.
5. Set the printer as default and print a test page.
6. If it still fails, remove the printer and add it again.'),

('Printer prints blank or faded pages', 'Printer', 'printer blank faded ink toner quality',
'1. Check ink or toner level and replace if low.
2. Run the printer''s built-in head cleaning from Printer Preferences > Maintenance.
3. Print a test page and check the result.
4. Make sure the correct paper type is selected.
5. If the problem continues, contact the hardware vendor.'),

('Forgot or need to reset account password', 'Account', 'password reset forgot locked account login',
'1. Confirm the identity of the user (employee ID, registered email or mobile number).
2. Use the self-service "Forgot password" link, or reset it from the admin console.
3. Set a temporary password and ask the user to change it at next login.
4. Remind the user: minimum 8 characters, mix of letters, numbers and a symbol.
5. If the account is locked, unlock it after 15 minutes or manually from the admin console.
6. Log the ticket with the action taken and close it after the user confirms login works.'),

('Software installation fails', 'Software', 'install installation setup error admin permission',
'1. Right-click the setup file and choose "Run as administrator".
2. Check there is at least 5 GB of free disk space.
3. Make sure the software supports your Windows version (32/64-bit).
4. Temporarily close antivirus while installing, then turn it back on.
5. Uninstall any older version first, restart, then install again.
6. If an error code appears, note it down and attach a screenshot to the ticket.'),

('MS Office (Word/Excel) not opening or crashing', 'Software', 'office word excel powerpoint crash not opening repair',
'1. Close the app fully from Task Manager, then open it again.
2. Open it in Safe Mode: hold Ctrl while opening the app.
3. Control Panel > Programs > Microsoft Office > Change > Quick Repair.
4. If it still fails, run Online Repair.
5. Check Windows Update is installed and restart the computer.'),

('Computer running very slow', 'Windows', 'slow performance lag disk cpu memory startup',
'1. Restart the computer.
2. Open Task Manager (Ctrl+Shift+Esc) and check which app uses the most CPU or memory.
3. Disable unnecessary startup apps (Task Manager > Startup).
4. Free up disk space with Disk Cleanup; keep at least 15% free.
5. Run a full antivirus scan.
6. Install pending Windows updates.'),

('Cannot access shared folder or network drive', 'Network', 'shared folder network drive lan access denied map',
'1. Check the computer is on the same LAN or VPN.
2. Ping the file server: ping servername. If it fails, check the network cable or Wi-Fi.
3. Try opening it using the IP address: \\\\192.168.x.x\\sharename.
4. Re-enter the username and password when prompted.
5. Ask the admin to confirm your account has permission to that folder.'),

('Windows not starting or stuck on loading screen', 'Windows', 'boot startup black screen windows loading repair',
'1. Disconnect all USB devices and restart.
2. Force shutdown 3 times to open the Windows Recovery Environment.
3. Choose Troubleshoot > Advanced options > Startup Repair.
4. If that fails, try Safe Mode (Startup Settings > press 4).
5. Escalate to L2 if disk errors or a corrupted OS are suspected.');
