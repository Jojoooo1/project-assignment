#!/bin/bash
set -e # exit on first error (used for return)

message() {
  echo -e "\n######################################################################"
  echo "# $1"
  echo "######################################################################"
}

getHotfixReleaseVersion() {
  # 1. Create array based on LATEST_TAG
  LATEST_TAG=$(git describe --tags "$(git rev-list --tags --max-count=1)") # gets tags across all branches, not just the current branch
  TAG_LIST=($(echo "$LATEST_TAG" | tr '.' ' '))

  # 2. Exit if invalid version
  [[ "${#TAG_LIST[@]}" -ne 2 ]] && echo "$LATEST_TAG is not a valid version" && exit 1

  # 3. Calculate release version
  V_MINOR=${TAG_LIST[0]}
  V_PATCH=$(( TAG_LIST[1] + 1 ))
  RELEASE_VERSION=${V_MINOR}.${V_PATCH}
}

message ">>> Starting hotfix"

[[ ! -x "$(command -v gh)" ]] && echo "gh not found, you need to install github CLI" && exit 1

gh auth status

# 1. Make sure branch is set to main
[[ $(git rev-parse --abbrev-ref HEAD) != "main" ]] && echo "ERROR: Checkout to main" && exit 1

# 2. Make sure branch is clean
[[ $(git status --porcelain) ]] && echo "ERROR: The branch is not clean, commit your changes before creating the release" && exit 1

message ">>> Pulling main"
git pull origin main
message ">>> Pulling tags"
git fetch --prune --tags

# 3. Get release version
getHotfixReleaseVersion

message ">>> Hotfix: $RELEASE_VERSION"

# 4. Start Github release
read -r -p "What is the name of the branch you want to create (should start with hotfix/):  " BRANCH_NAME
[[ $BRANCH_NAME != hotfix/* ]] && echo "'$BRANCH_NAME' is invalid, it should start with 'hotfix/')" && exit 1

read -r -p "Are you sure you want to create the branch '$BRANCH_NAME' [Y/n]:  " RESPONSE
if [[ $RESPONSE =~ ^([yY][eE][sS]|[yY])$ ]]; then

  message ">>>>> Creating branch '$BRANCH_NAME' from main..."
  git checkout -b "$BRANCH_NAME" main
  git commit --allow-empty -m "Hotfix - $RELEASE_VERSION"
  git push origin "$BRANCH_NAME"
  gh pr create --base main --head "$BRANCH_NAME" --title "Hotfix - $RELEASE_VERSION" --fill

else

  message "Action cancelled exiting..."
  exit 1

fi
