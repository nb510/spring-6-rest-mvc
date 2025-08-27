for branch in $(git branch -r | grep 'upstream/' | sed 's/ *upstream\///'); do
    git checkout -B $branch upstream/$branch
    git push origin $branch --force
done
