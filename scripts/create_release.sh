#!/bin/bash
set -e # exit on first error (used for return

message() {
  echo -e "\n######################################################################"
  echo "# $1"
  echo "######################################################################"
}

getReleaseVersion() {
  # Create array based on LATEST_TAG
  LATEST_TAG=$(git describe --tags "$(git rev-list --tags --max-count=1)") # gets tags across all branches, not just the current branch
  TAG_LIST=($(echo "$LATEST_TAG" | tr '.' ' '))

  # Exit if invalid version
  [[ "${#TAG_LIST[@]}" -ne 2 ]] && echo "$LATEST_TAG is not a valid version" && exit 1

  # Calculate release version
  V_MINOR=$(( TAG_LIST[0] + 1 ))
  V_PATCH=0
  RELEASE_VERSION=${V_MINOR}.${V_PATCH}
}

message ">>> Starting release"

[[ ! -x "$(command -v gh)" ]] && echo "gh not found, you need to install github CLI" && exit 1

gh auth status

# 1. Make sure branch is set to develop
[[ $(git rev-parse --abbrev-ref HEAD) != "develop" ]] && echo "ERROR: Checkout to develop" && exit 1

# 2. Make sure branch is clean
[[ $(git status --porcelain) ]] && echo "ERROR: The branch is not clean, commit your changes before creating the release" && exit 1

message ">>> Pulling develop"
git pull origin develop ##
message ">>> Pulling tags"
git fetch --prune --prune-tags origin

# 3. Get release version
getReleaseVersion

message ">>> Release: $RELEASE_VERSION"

# 4. Start Github release
read -r -p "Last release version was '$LATEST_TAG', do you want to create '$RELEASE_VERSION' [Y/n]:  " RESPONSE
if [[ $RESPONSE =~ ^([yY][eE][sS]|[yY])$ ]]; then

  BRANCH_NAME="release/$RELEASE_VERSION"
  message ">>>>> Creating branch '$BRANCH_NAME' from develop..."

  git checkout -b "$BRANCH_NAME" develop
  git push origin "$BRANCH_NAME"
  gh pr create --base main --head "$BRANCH_NAME" --title "Release - $RELEASE_VERSION" --fill

else

    message "Action cancelled exiting..."
    exit 1

fi

