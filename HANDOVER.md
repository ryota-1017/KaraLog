# 🎤 KaraLog プロジェクト引き継ぎ書 (Handover Guide)

本ドキュメントは、カラオケ記録・現場アシスタントAndroidアプリ **「KaraLog（カラログ）」** の開発環境・GitHubリポジトリ・ビルド＆リリースフロー・アーキテクチャ設計を、別のアカウントや別のPC環境へ完全に引き継ぎ、同一の状態で開発・運用を継続するための手順書です。

---

## 📌 1. プロジェクト基本情報

| 項目 | 内容 |
| :--- | :--- |
| **アプリ名** | KaraLog (カラログ) |
| **最新バージョン** | **v2.0.2** (versionCode: 15) |
| **パッケージ名** | `com.karalog.app` |
| **対応OS** | Android 7.0+ (minSdk 24, targetSdk 35) |
| **開発言語** | Kotlin 2.0.21 |
| **UIフレームワーク** | Jetpack Compose + Material3 |
| **データベース** | Room (SQLite) ※完全ローカル動作・外部通信なし |
| **現在リポジトリ** | `https://github.com/ryota-1017/KaraLog.git` |
| **配布方式** | GitHub Releases (APK直接配布 / Obtainium による自動更新対応) |

---

## 🛠️ 2. 技術スタック・開発環境の要件

移行先のPC・環境で必要となるツールおよびバージョンです。

### 必要なソフトウェア
- **OS**: Windows 10/11 (または macOS / Linux)
- **JDK**: **Java 17** (Android Studio 付属の JBR 17 推奨)
  - パス例 (Windows): `C:\Program Files\Android\Android Studio1\jbr` または `C:\Program Files\Android\Android Studio\jbr`
- **Android SDK**: Android SDK Platform 35, Build-Tools 35.0.0
  - パス例: `C:\Users\<USER>\AppData\Local\Android\Sdk`
- **ビルドツール**: Gradle 8.11.1 (Gradle Wrapper `gradlew` 同梱)
- **Git**: 2.40 以上
- **Python**: 3.9 以上 (GitHub Releases への自動アップロードスクリプト実行用)

### 主なライブラリ構成 (`app/build.gradle.kts`)
- `androidx.compose.ui:ui` & `androidx.compose.material3:material3`
- `androidx.room:room-runtime:2.6.1` & `androidx.room:room-ktx:2.6.1` (KSPによるコード生成)
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7`
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1`

---

## 🚀 3. 別アカウントへの移行手順 (ステップ・バイ・ステップ)

新しいGitHubアカウントへリポジトリを移行し、開発を再開する手順です。

### ステップ 3-1: 新しいアカウントで空リポジトリを作成
1. 移行先の GitHub アカウントにログインします。
2. 新規リポジトリ作成（New repository）を開きます。
3. リポジトリ名を `KaraLog`（または任意の名前）にし、**「Add a README file」等のチェックはすべて外して空の状態で作成**します。
4. 作成後のリポジトリURLを取得します（例: `https://github.com/<NEW_USERNAME>/KaraLog.git`）。

### ステップ 3-2: ローカルリポジトリのリモートURLを変更してプッシュ
既存のソースコードがあるPC（またはローカルクローン）で以下を実行します：

```bash
# 作業ディレクトリに移動
cd C:\Users\ryota\AndroidStudioProjects\KaraLog

# 現在のリモートURLを確認
git remote -v

# 新しいGitHubアカウントのリポジトリURLに変更
git remote set-url origin https://github.com/<NEW_USERNAME>/KaraLog.git

# メインブランチと全タグを新リポジトリへ一括プッシュ
git push -u origin main --tags
```
※認証を求められた場合は、新しいアカウントの **Personal Access Token (classic: `repo` 権限)** または GitHub CLI (`gh auth login`) でログインしてください。

### ステップ 3-3: 新しいPC環境で一からセットアップする場合
もし新しいPCで開発を開始する場合は、以下を実行します：

```bash
git clone https://github.com/<NEW_USERNAME>/KaraLog.git
cd KaraLog
```

---

## ⚙️ 4. ビルド & 動作確認手順

### コマンドラインでのビルド (Windows PowerShell / CMD)
```powershell
# 環境変数をセット (パスは環境に合わせて調整)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"
$env:ANDROID_HOME = "C:\Users\ryota\AppData\Local\Android\Sdk"

# デバッグAPKのビルド
.\gradlew.bat assembleDebug
```
ビルドが成功すると、以下の場所に APK が生成されます：
`app\build\outputs\apk\debug\app-debug.apk`

---

## 📦 5. バージョンアップ & リリース手順

機能追加や修正を行った後のバージョンアップとGitHub Releasesへの公開手順です。

### ステップ 5-1: バージョン番号の更新
`app/build.gradle.kts` の `defaultConfig` ブロックを開き、インクリメントします。
```kotlin
defaultConfig {
    applicationId = "com.karalog.app"
    minSdk = 24
    targetSdk = 35
    versionCode = 16          // 前回より +1
    versionName = "2.0.3"     // 適切なセマンティックバージョン
    ...
}
```

### ステップ 5-2: APKのビルド
```powershell
.\gradlew.bat assembleDebug
```

### ステップ 5-3: リリース用APKの配置
```powershell
# release ディレクトリへコピー
Copy-Item "app\build\outputs\apk\debug\app-debug.apk" "release\KaraLog-v2.0.3.apk" -Force
```

### ステップ 5-4: コミットとGitタグの作成・プッシュ
```bash
git add -A
git commit -m "feat: KaraLog v2.0.3 - <更新内容>"
git tag -a v2.0.3 -m "KaraLog v2.0.3"
git push origin main --tags
```

### ステップ 5-5: GitHub Releases への自動アップロード
プロジェクトルートにある `release_uploader.py` を実行するだけで、新アカウントのリポジトリURLを自動判別し、Release作成とAPKアセットの添付が完了します。

```bash
python release_uploader.py
```

---

## 📂 6. ディレクトリ構成と役割

```
KaraLog/
├── app/
│   ├── build.gradle.kts           # アプリ依存関係、versionCode, versionName
│   └── src/main/
│       ├── AndroidManifest.xml    # アプリ権限・Activity定義
│       ├── res/
│       │   ├── drawable/
│       │   │   ├── ic_launcher_background.xml  # アプリアイコン背景（ディープブラック＋微細サークル）
│       │   │   ├── ic_launcher_foreground.xml  # アプリアイコン前景（音波イコライザー×スタジオマイク）
│       │   │   └── ic_launcher_monochrome.xml  # Android 13+ テーマ別アイコン
│       │   └── values/strings.xml
│       └── java/com/karalog/app/
│           ├── KaraLogApplication.kt     # アプリ基底クラス（DBとRepositoryの初期化）
│           ├── MainActivity.kt           # 全画面のルーティング、ダイアログ制御、テーマ判定
│           ├── MainViewModel.kt          # 全画面の状態管理、DB操作、セッション管理、キュー管理
│           ├── data/
│           │   ├── local/
│           │   │   ├── KaraLogDatabase.kt       # Room データベース定義
│           │   │   ├── PopularSongsSeed.kt      # 人気ランキング定番35曲のプリセットデータ
│           │   │   ├── dao/
│           │   │   │   ├── SongDao.kt           # 楽曲テーブルのCRUD操作
│           │   │   │   └── SingingRecordDao.kt  # 歌唱記録・スコアのCRUD操作
│           │   └── entity/
│           │       ├── SongEntity.kt        # 楽曲テーブル (title, artist, key, isKept, isDam, etc.)
│           │       └── SingingRecordEntity.kt # 採点履歴テーブル (score, machineType, key, date)
│           ├── data/model/
│           │   └── SongWithStats.kt         # 楽曲と過去最高点・平均点・最新点数を結合したUI用モデル
│           ├── data/repository/
│           │   └── KaraLogRepository.kt     # Room DAOへのアクセスを集約・抽象化
│           ├── ui/
│           │   ├── theme/Theme.kt               # カラースキーム（True Black / Soft Slate / Accent）
│           │   ├── components/
│           │   │   ├── BottomNavBar.kt          # フローティング・アイランド型ボトムナビ
│           │   │   ├── HeaderBar.kt             # ヘッダーロゴ、Start/Finishボタン、設定アイコン
│           │   │   ├── SongCard.kt              # 楽曲カード（歌う、予約、キープ、試聴、削除）
│           │   │   ├── NowSingingView.kt        # 歌唱中画面（デンモク番号、キー調整、機種切替）
│           │   │   ├── ScoreInputSheet.kt       # 小数点自動挿入の高速テンキーシート
│           │   │   ├── ScoreResultModal.kt      # 歌唱後の結果表示モーダル
│           │   │   ├── SessionSummaryDialog.kt  # カラオケ終了時のサマリーダイアログ
│           │   │   └── ListenMusicSheet.kt      # YouTube Music / Spotify 試聴ボトムシート
│           │   └── screens/
│           │       ├── start/StartScreen.kt     # 入室時の機種選択（DAM/JOYSOUND）画面
│           │       ├── home/HomeScreen.kt       # ホーム（予約キュー、おすすめ、ランキング一覧）
│           │       ├── search/SearchScreen.kt   # 楽曲検索、機種・カテゴリフィルター、曲登録
│           │       ├── history/HistoryScreen.kt # 歌唱履歴タイムライン
│           │       └── settings/SettingsScreen.kt# テーマ変更、機種設定、深夜3時リセット設定
│           └── util/
│               ├── HapticHelper.kt              # タップ時の心地よい触覚フィードバック
│               ├── IntentHelper.kt              # Spotify / YouTube Music 外部アプリ起動
│               └── ShareCardHelper.kt           # スコア共有用カード生成ヘルパー
├── release/                                     # 生成された公開用APKの保管先
├── release_uploader.py                          # GitHub Releases アップロード用Pythonスクリプト
├── HANDOVER.md                                  # 本引き継ぎ書
└── README.md                                    # プロジェクト紹介 & ダウンロードリンク
```

---

## 💡 7. 開発・保守上の重要な注意点（Gotchas & Rules）

コードの改修時に必ず知っておくべき仕様と落とし穴のまとめです。

### 1. キープ（ブックマーク）ボタンの反転仕様
- `KaraLogRepository.kt` の `toggleKeptStatus(songId, currentKept)` は、内部で `songDao.updateKeptStatus(songId, !currentKept)` を実行します。
- したがって、呼び出し側（`MainActivity.kt`）では **`!song.isKept` ではなく現在の値 `song.isKept` をそのまま渡す必要があります**（二重で反転させると元の値に戻ってしまうため）。

### 2. ボトムバーのテーマ連動
- `BottomNavBar.kt` 内部で `isSystemInDarkTheme()` を直接評価してはいけません。
- ユーザーがアプリ内の設定画面で「ダーク (漆黒)」または「ライト」を明示選択している場合があるため、必ず `MainActivity.kt` で算出した `isDark: Boolean` を引数として `BottomNavBar` に渡す必要があります。

### 3. ボタンの枠線（「変な線」防止ルール）
- Jetpack Compose の `Surface` で `modifier.border(...)` と `color = TextMain` を重ねて指定すると、角丸のクリップとアンチエイリアスの境界線がズレて「二重枠線（ジャギー）」が発生します。
- **選択時（アクティブ）**: `border = null`（枠線なし、ソリッドベタ塗り背景）
- **未選択時（非アクティブ）**: `border = BorderStroke(1.dp, CardBorder)`
- タップ操作は `Modifier.clickable` ではなく `Surface(onClick = { ... })` を使用することで、リップル効果と形状クリップが最も綺麗に一体化します。

### 4. 深夜3時自動リセットロジック
- カラオケボックス利用時の日付跨ぎに対応するため、深夜3:00（午前3時）をセッションの区切りとしています。
- `MainViewModel.check3AmAutoReset()` により、アプリ起動時に前回のセッション開始時刻から深夜3時を跨いでいるか判定し、跨いでいれば自動リセットします（設定画面からON/OFF可能）。

---

## 📲 8. Obtainium（自動更新）の設定

ユーザーが [Obtainium](https://github.com/ImranR98/Obtainium) で最新APKの自動アップデートを受け取るためのURL構成です：

- **App Source URL**: `https://github.com/<NEW_USERNAME>/KaraLog`
- GitHub Releases に新しいタグ付きで APK（`KaraLog-vX.X.X.apk`）がアップロードされると、Obtainium が自動検知してユーザーに通知・ワンタップ更新を提供します。
