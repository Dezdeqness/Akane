#!/usr/bin/env bash
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${HERE}/../.." && pwd)"
WORK="${HERE}/.work-macos"

if [ "$(uname -m)" != "arm64" ]; then
  echo "This kit targets Apple Silicon only; got arch $(uname -m)." >&2
  exit 1
fi
PREFIX="darwin-aarch64"
OUT="${REPO_ROOT}/features/videoplayer/src/desktopMain/resources/mpv/${PREFIX}"
mkdir -p "${OUT}"

# Toolchain: meson/ninja/nasm + autotools (libass builds via autoreconf). The
# libass/freetype/harfbuzz/fribidi/lcms2/uchardet dylibs get folded into the final
# artifact by dylibbundler below.
# autotools installed on their own so a failure here isn't swallowed by "|| true".
brew install autoconf automake libtool
brew install meson ninja nasm pkg-config \
  libass freetype harfbuzz fribidi little-cms2 uchardet dylibbundler || true

export PATH="$(brew --prefix)/bin:${PATH}"

# Homebrew installs libtool's tools g-prefixed (glibtoolize). libass' autogen
# calls libtoolize, so expose the un-prefixed names via libtool's gnubin dir…
LIBTOOL_GNUBIN="$(brew --prefix)/opt/libtool/libexec/gnubin"
[ -d "${LIBTOOL_GNUBIN}" ] && export PATH="${LIBTOOL_GNUBIN}:${PATH}"

# …and fall back to a symlink shim if gnubin isn't there.
if ! command -v libtoolize >/dev/null && command -v glibtoolize >/dev/null; then
  SHIM="${WORK}/shim-bin"
  mkdir -p "${SHIM}"
  ln -sf "$(command -v glibtoolize)" "${SHIM}/libtoolize"
  export PATH="${SHIM}:${PATH}"
fi

for tool in autoreconf libtoolize nasm meson ninja; do
  command -v "$tool" >/dev/null || {
    echo "Required build tool '$tool' not found on PATH after brew install." >&2
    echo "Fix: brew install autoconf automake libtool meson ninja nasm" >&2
    exit 1
  }
done

# 2. mpv-build with our trimmed options.
if [ ! -d "${WORK}/mpv-build" ]; then
  git clone https://github.com/mpv-player/mpv-build.git "${WORK}/mpv-build"
fi
cd "${WORK}/mpv-build"
cp "${HERE}/ffmpeg_options" ffmpeg_options
cp "${HERE}/mpv_options"    mpv_options
./update
# Incremental build (skips already-built ffmpeg/libass/libplacebo on re-runs).
# If it ever gets into a bad state, `rm -rf scripts/mpv/.work-macos` and re-run.
./build -j"$(sysctl -n hw.ncpu)"

# 3. Copy the built libmpv into resources and fold every dependency next to it,
#    rewriting install names to @loader_path/ so the whole folder is relocatable.
BUILT="$(find "${WORK}/mpv-build/mpv/build" -name 'libmpv.*.dylib' | head -1)"
[ -n "${BUILT}" ] || { echo "libmpv build output not found" >&2; exit 1; }
cp "${BUILT}" "${OUT}/libmpv.2.dylib"
dylibbundler -of -b -x "${OUT}/libmpv.2.dylib" -d "${OUT}" -p '@loader_path/'

# dylibbundler rewrites every pre-existing LC_RPATH to '@loader_path/', which leaves
# DUPLICATE '@loader_path/' rpaths — and dyld refuses to load a dylib that has them.
# Collapse each dylib's rpaths to a single '@loader_path/', strip, then re-sign
# (strip/install_name_tool invalidate the ad-hoc signature dylibbundler applied).
for f in "${OUT}"/*.dylib; do
  while install_name_tool -delete_rpath '@loader_path/' "$f" 2>/dev/null; do :; done
  install_name_tool -add_rpath '@loader_path/' "$f" 2>/dev/null || true
  strip -x -S "$f" 2>/dev/null || true
  codesign --force --sign - "$f" 2>/dev/null || true
done

echo
echo "OK -> ${OUT}/libmpv.2.dylib (+ dependency dylibs in the same folder)"
echo "Verify it is self-contained:"
echo "  otool -L '${OUT}/libmpv.2.dylib'   # only /usr/lib, /System and @loader_path entries"
