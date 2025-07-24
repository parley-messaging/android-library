# Publishing

In order to publish, several checks should be done.

## Package

- [ ] Ensure all the tests run successfully.
- [ ] Ensure `build.gradle` has the correct version.
- [ ] Ensure `CHANGELOG.md` is updated.
- [ ] Ensure `README.md` is updated.
- [ ] Ensure there is a valid SSL pinning certificate for the upcoming months inside the release.

## Example

- [ ] Ensure `app/build.gradle` uses the local library reference to Parley.
- [ ] Ensure there is a valid SSL pinning certificate for the upcoming months inside the example project.

## Publish

- [ ] Merge with the master branch and create the release on GitHub. 
- [ ] Trigger the build on JitPack to compile the library. 
