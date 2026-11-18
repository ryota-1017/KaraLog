import subprocess
import urllib.request
import json
import re
import os
import sys

def get_git_credentials():
    proc = subprocess.Popen(['git', 'credential', 'fill'], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    out, _ = proc.communicate(input='protocol=https\nhost=github.com\n\n')
    creds = dict(line.split('=', 1) for line in out.splitlines() if '=' in line)
    return creds.get('password')

def get_repo_slug():
    proc = subprocess.run(['git', 'config', '--get', 'remote.origin.url'], capture_output=True, text=True)
    url = proc.stdout.strip()
    match = re.search(r'github\.com[/:]([\w.-]+)/([\w.-]+?)(\.git)?$', url)
    if match:
        return f"{match.group(1)}/{match.group(2)}"
    return None

def main():
    token = get_git_credentials()
    if not token:
        token = os.environ.get("GITHUB_TOKEN")
    
    if not token:
        print("[Error] GitHub token not found. Please log in via Git Credential Manager or set GITHUB_TOKEN.")
        sys.exit(1)

    repo = get_repo_slug()
    if not repo:
        repo = "ryota-1017/KaraLog"
    
    # Get latest git tag
    proc = subprocess.run(['git', 'describe', '--tags', '--abbrev=0'], capture_output=True, text=True)
    tag = proc.stdout.strip() if proc.returncode == 0 else "v2.0.2"
    
    print(f"Target Repository: {repo}")
    print(f"Target Tag: {tag}")

    apk_path = f"release/KaraLog-{tag}.apk"
    if not os.path.exists(apk_path):
        # Fallback to app-debug.apk
        apk_path = "app/build/outputs/apk/debug/app-debug.apk"
        if not os.path.exists(apk_path):
            print(f"[Error] APK file not found at release/KaraLog-{tag}.apk or {apk_path}")
            sys.exit(1)

    apk_name = f"KaraLog-{tag}.apk"
    name = f"KaraLog {tag}"
    body = f"## KaraLog {tag} Release\n\nAutomated release package for {tag}."

    headers = {
        'Authorization': f'token {token}',
        'Accept': 'application/vnd.github.v3+json',
        'User-Agent': 'KaraLog-Release-Uploader'
    }

    # 1. Create or fetch Release
    url = f'https://api.github.com/repos/{repo}/releases'
    payload = json.dumps({
        'tag_name': tag,
        'name': name,
        'body': body,
        'draft': False,
        'prerelease': False
    }).encode('utf-8')

    req = urllib.request.Request(url, data=payload, headers=headers, method='POST')
    try:
        with urllib.request.urlopen(req) as resp:
            release_data = json.loads(resp.read().decode('utf-8'))
            upload_url = release_data['upload_url'].split('{')[0]
            print(f"Release created: {release_data.get('html_url')}")
    except Exception:
        req = urllib.request.Request(f'https://api.github.com/repos/{repo}/releases/tags/{tag}', headers=headers)
        with urllib.request.urlopen(req) as resp:
            release_data = json.loads(resp.read().decode('utf-8'))
            upload_url = release_data['upload_url'].split('{')[0]
            print(f"Existing release found: {release_data.get('html_url')}")

    # 2. Upload APK Asset
    with open(apk_path, 'rb') as f:
        apk_data = f.read()

    upload_headers = {
        'Authorization': f'token {token}',
        'Content-Type': 'application/vnd.android.package-archive',
        'User-Agent': 'KaraLog-Release-Uploader'
    }

    req_upload = urllib.request.Request(f'{upload_url}?name={apk_name}', data=apk_data, headers=upload_headers, method='POST')
    try:
        with urllib.request.urlopen(req_upload) as resp:
            asset_data = json.loads(resp.read().decode('utf-8'))
            print(f"Asset uploaded successfully: {asset_data.get('browser_download_url')}")
    except Exception as e:
        print(f"Asset upload note (might already exist): {e}")

if __name__ == "__main__":
    main()
