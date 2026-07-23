#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repository_root="$(cd -- "${script_dir}/.." && pwd)"

"${script_dir}/build-project-site.sh"
node --check "${repository_root}/privacy-site/assets/project.js"
node --check "${repository_root}/privacy-site/assets/theme-init.js"
node --check "${repository_root}/scripts/test-project-site-browser.mjs"
node --check "${repository_root}/scripts/test-project-site-firefox.mjs"
python3 "${script_dir}/validate-project-site.py" "${repository_root}/_site"

"${script_dir}/prepare-pages-transition.sh" "_pages-transition"
python3 "${script_dir}/validate-pages-transition.py" "${repository_root}/_pages-transition"
