/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jdpgrailsdev.oasis.timeline.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import twitter4j.AccountSettings;
import twitter4j.Category;
import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.Friendship;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.OEmbed;
import twitter4j.OEmbedRequest;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.api.DirectMessagesResources;
import twitter4j.api.FavoritesResources;
import twitter4j.api.FriendsFollowersResources;
import twitter4j.api.HelpResources;
import twitter4j.api.ListsResources;
import twitter4j.api.PlacesGeoResources;
import twitter4j.api.SavedSearchesResources;
import twitter4j.api.SearchResource;
import twitter4j.api.SpamReportingResource;
import twitter4j.api.SuggestedUsersResources;
import twitter4j.api.TimelinesResources;
import twitter4j.api.TrendsResources;
import twitter4j.api.TweetsResources;
import twitter4j.api.UsersResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.util.function.Consumer;

@SuppressWarnings({
  "PMD.ExcessiveClassLength",
  "PMD.CyclomaticComplexity",
  "PMD.RedundantFieldInitializer",
  "PMD.AvoidUncheckedExceptionsInSignatures",
  "PMD.ReturnEmptyCollectionRatherThanNull",
  "PMD.MissingSerialVersionUID",
  "PMD.ShortVariable"
})
public class MockTwitter implements Twitter {

  private final List<String> tweets = new ArrayList<>();

  private boolean throwException = false;

  public void reset() {
    tweets.clear();
    throwException = false;
  }

  public List<String> getTweets() {
    return List.copyOf(tweets);
  }

  @Override
  public TimelinesResources timelines() {
    return null;
  }

  @Override
  public TweetsResources tweets() {
    return null;
  }

  @Override
  public SearchResource search() {
    return null;
  }

  @Override
  public QueryResult search(final Query query) throws TwitterException {
    return null;
  }

  @Override
  public DirectMessagesResources directMessages() {
    return null;
  }

  @Override
  public FriendsFollowersResources friendsFollowers() {
    return null;
  }

  @Override
  public UsersResources users() {
    return null;
  }

  @Override
  public SuggestedUsersResources suggestedUsers() {
    return null;
  }

  @Override
  public FavoritesResources favorites() {
    return null;
  }

  @Override
  public ListsResources list() {
    return null;
  }

  @Override
  public SavedSearchesResources savedSearches() {
    return null;
  }

  @Override
  public PlacesGeoResources placesGeo() {
    return null;
  }

  @Override
  public TrendsResources trends() {
    return null;
  }

  @Override
  public SpamReportingResource spamReporting() {
    return null;
  }

  @Override
  public HelpResources help() {
    return null;
  }

  @Override
  public String getScreenName() throws TwitterException, IllegalStateException {
    return null;
  }

  @Override
  public long getId() throws TwitterException, IllegalStateException {
    return 0L;
  }

  @Override
  public void addRateLimitStatusListener(final RateLimitStatusListener listener) {
    // Not implemented
  }

  @Override
  public void onRateLimitStatus(final Consumer<RateLimitStatusEvent> action) {
    // Not implemented
  }

  @Override
  public void onRateLimitReached(final Consumer<RateLimitStatusEvent> action) {
    // Not implemented
  }

  @Override
  public Authorization getAuthorization() {
    return null;
  }

  @Override
  public Configuration getConfiguration() {
    return null;
  }

  @Override
  public ResponseList<DirectMessage> getDirectMessages() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<DirectMessage> getDirectMessages(final Paging paging)
      throws TwitterException {
    return null;
  }

  @Override
  public DirectMessageList getDirectMessages(final int count) throws TwitterException {
    return null;
  }

  @Override
  public DirectMessageList getDirectMessages(final int count, final String cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<DirectMessage> getSentDirectMessages() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<DirectMessage> getSentDirectMessages(final Paging paging)
      throws TwitterException {
    return null;
  }

  @Override
  public DirectMessage showDirectMessage(final long id) throws TwitterException {
    return null;
  }

  @Override
  public DirectMessage destroyDirectMessage(final long id) throws TwitterException {
    return null;
  }

  @Override
  public DirectMessage sendDirectMessage(final long userId, final String text)
      throws TwitterException {
    return null;
  }

  @Override
  public DirectMessage sendDirectMessage(final long userId, final String text, final long mediaId)
      throws TwitterException {
    return null;
  }

  @Override
  public DirectMessage sendDirectMessage(final String screenName, final String text)
      throws TwitterException {
    return null;
  }

  @Override
  public InputStream getDMImageAsStream(final String url) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getFavorites() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getFavorites(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getFavorites(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getFavorites(final Paging paging) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getFavorites(final long userId, final Paging paging)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getFavorites(final String screenName, final Paging paging)
      throws TwitterException {
    return null;
  }

  @Override
  public Status createFavorite(final long id) throws TwitterException {
    return null;
  }

  @Override
  public Status destroyFavorite(final long id) throws TwitterException {
    return null;
  }

  @Override
  public IDs getNoRetweetsFriendships() throws TwitterException {
    return null;
  }

  @Override
  public IDs getFriendsIDs(final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getFriendsIDs(final long userId, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getFriendsIDs(final long userId, final long cursor, final int count)
      throws TwitterException {
    return null;
  }

  @Override
  public IDs getFriendsIDs(final String screenName, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getFriendsIDs(final String screenName, final long cursor, final int count)
      throws TwitterException {
    return null;
  }

  @Override
  public IDs getFollowersIDs(final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getFollowersIDs(final long userId, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getFollowersIDs(final long userId, final long cursor, final int count)
      throws TwitterException {
    return null;
  }

  @Override
  public IDs getFollowersIDs(final String screenName, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getFollowersIDs(final String screenName, final long cursor, final int count)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Friendship> lookupFriendships(final long... ids) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Friendship> lookupFriendships(final String... screenNames)
      throws TwitterException {
    return null;
  }

  @Override
  public IDs getIncomingFriendships(final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getOutgoingFriendships(final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public User createFriendship(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User createFriendship(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public User createFriendship(final long userId, final boolean follow) throws TwitterException {
    return null;
  }

  @Override
  public User createFriendship(final String screenName, final boolean follow)
      throws TwitterException {
    return null;
  }

  @Override
  public User destroyFriendship(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User destroyFriendship(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public Relationship updateFriendship(
      final long userId, final boolean enableDeviceNotification, final boolean retweets)
      throws TwitterException {
    return null;
  }

  @Override
  public Relationship updateFriendship(
      final String screenName, final boolean enableDeviceNotification, final boolean retweets)
      throws TwitterException {
    return null;
  }

  @Override
  public Relationship showFriendship(final long sourceId, final long targetId)
      throws TwitterException {
    return null;
  }

  @Override
  public Relationship showFriendship(final String sourceScreenName, final String targetScreenName)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFriendsList(final long userId, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFriendsList(
      final long userId, final long cursor, final int count) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFriendsList(final String screenName, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFriendsList(
      final String screenName, final long cursor, final int count) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFriendsList(
      final long userId,
      final long cursor,
      final int count,
      final boolean skipStatus,
      final boolean includeUserEntities)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFriendsList(
      final String screenName,
      final long cursor,
      final int count,
      final boolean skipStatus,
      final boolean includeUserEntities)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFollowersList(final long userId, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFollowersList(final String screenName, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFollowersList(
      final long userId, final long cursor, final int count) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFollowersList(
      final String screenName, final long cursor, final int count) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFollowersList(
      final long userId,
      final long cursor,
      final int count,
      final boolean skipStatus,
      final boolean includeUserEntities)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getFollowersList(
      final String screenName,
      final long cursor,
      final int count,
      final boolean skipStatus,
      final boolean includeUserEntities)
      throws TwitterException {
    return null;
  }

  @Override
  public TwitterAPIConfiguration getAPIConfiguration() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Language> getLanguages() throws TwitterException {
    return null;
  }

  @Override
  public String getPrivacyPolicy() throws TwitterException {
    return null;
  }

  @Override
  public String getTermsOfService() throws TwitterException {
    return null;
  }

  @Override
  public Map<String, RateLimitStatus> getRateLimitStatus() throws TwitterException {
    return null;
  }

  @Override
  public Map<String, RateLimitStatus> getRateLimitStatus(final String... resources)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<UserList> getUserLists(final String listOwnerScreenName)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<UserList> getUserLists(
      final String listOwnerScreenName, final boolean reverse) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<UserList> getUserLists(final long listOwnerUserId) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<UserList> getUserLists(final long listOwnerUserId, final boolean reverse)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserListStatuses(final long listId, final Paging paging)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserListStatuses(
      final long ownerId, final String slug, final Paging paging) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserListStatuses(
      final String ownerScreenName, final String slug, final Paging paging)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListMember(final long listId, final long userId)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListMember(final long listId, final String screenName)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListMember(final long ownerId, final String slug, final long userId)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListMember(
      final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListMembers(final long listId, final String[] screenNames)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListMembers(final long listId, final long[] userIds)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListMembers(
      final String ownerScreenName, final String slug, final String[] screenNames)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(
      final long listMemberId, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(
      final long listMemberId, final int count, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(
      final String listMemberScreenName, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(
      final String listMemberScreenName, final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(
      final String listMemberScreenName, final long cursor, final boolean filterToOwnedLists)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(
      final String listMemberScreenName,
      final int count,
      final long cursor,
      final boolean filterToOwnedLists)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(
      final long listMemberId, final long cursor, final boolean filterToOwnedLists)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(
      final long listMemberId, final int count, final long cursor, final boolean filterToOwnedLists)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(final long listId, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(
      final long listId, final int count, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(
      final long listId, final int count, final long cursor, final boolean skipStatus)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(
      final long ownerId, final String slug, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(
      final long ownerId, final String slug, final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(
      final long ownerId,
      final String slug,
      final int count,
      final long cursor,
      final boolean skipStatus)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(
      final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(
      final String ownerScreenName, final String slug, final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(
      final String ownerScreenName,
      final String slug,
      final int count,
      final long cursor,
      final boolean skipStatus)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListSubscription(final long listId) throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListSubscription(final long ownerId, final String slug)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListSubscription(final String ownerScreenName, final String slug)
      throws TwitterException {
    return null;
  }

  @Override
  public User showUserListSubscription(final long listId, final long userId)
      throws TwitterException {
    return null;
  }

  @Override
  public User showUserListSubscription(final long ownerId, final String slug, final long userId)
      throws TwitterException {
    return null;
  }

  @Override
  public User showUserListSubscription(
      final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListSubscription(final long listId) throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListSubscription(final long ownerId, final String slug)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserListSubscription(final String ownerScreenName, final String slug)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMembers(final long listId, final long... userIds)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMembers(
      final long ownerId, final String slug, final long... userIds) throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMembers(
      final String ownerScreenName, final String slug, final long... userIds)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMembers(final long listId, final String... screenNames)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMembers(
      final long ownerId, final String slug, final String... screenNames) throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMembers(
      final String ownerScreenName, final String slug, final String... screenNames)
      throws TwitterException {
    return null;
  }

  @Override
  public User showUserListMembership(final long listId, final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User showUserListMembership(final long ownerId, final String slug, final long userId)
      throws TwitterException {
    return null;
  }

  @Override
  public User showUserListMembership(
      final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(final long listId, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(
      final long listId, final int count, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(
      final long listId, final int count, final long cursor, final boolean skipStatus)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(
      final long ownerId, final String slug, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(
      final long ownerId, final String slug, final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(
      final long ownerId,
      final String slug,
      final int count,
      final long cursor,
      final boolean skipStatus)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(
      final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(
      final String ownerScreenName, final String slug, final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getUserListMembers(
      final String ownerScreenName,
      final String slug,
      final int count,
      final long cursor,
      final boolean skipStatus)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMember(final long listId, final long userId)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMember(final long ownerId, final String slug, final long userId)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserListMember(
      final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserList(final long listId) throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserList(final long ownerId, final String slug) throws TwitterException {
    return null;
  }

  @Override
  public UserList destroyUserList(final String ownerScreenName, final String slug)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList updateUserList(
      final long listId,
      final String newListName,
      final boolean isPublicList,
      final String newDescription)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList updateUserList(
      final long ownerId,
      final String slug,
      final String newListName,
      final boolean isPublicList,
      final String newDescription)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList updateUserList(
      final String ownerScreenName,
      final String slug,
      final String newListName,
      final boolean isPublicList,
      final String newDescription)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList createUserList(
      final String listName, final boolean isPublicList, final String description)
      throws TwitterException {
    return null;
  }

  @Override
  public UserList showUserList(final long listId) throws TwitterException {
    return null;
  }

  @Override
  public UserList showUserList(final long ownerId, final String slug) throws TwitterException {
    return null;
  }

  @Override
  public UserList showUserList(final String ownerScreenName, final String slug)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(
      final String listSubscriberScreenName, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(
      final String listSubscriberScreenName, final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(
      final long listSubscriberId, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(
      final long listSubscriberId, final int count, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(
      final String listOwnerScreenName, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(
      final String listOwnerScreenName, final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(
      final long listOwnerId, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(
      final long listOwnerId, final int count, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public Place getGeoDetails(final String placeId) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Place> reverseGeoCode(final GeoQuery query) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Place> searchPlaces(final GeoQuery query) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Place> getSimilarPlaces(
      final GeoLocation location,
      final String name,
      final String containedWithin,
      final String streetAddress)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<SavedSearch> getSavedSearches() throws TwitterException {
    return null;
  }

  @Override
  public SavedSearch showSavedSearch(final long id) throws TwitterException {
    return null;
  }

  @Override
  public SavedSearch createSavedSearch(final String query) throws TwitterException {
    return null;
  }

  @Override
  public SavedSearch destroySavedSearch(final long id) throws TwitterException {
    return null;
  }

  @Override
  public User reportSpam(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User reportSpam(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> getUserSuggestions(final String categorySlug) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Category> getSuggestedUserCategories() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> getMemberSuggestions(final String categorySlug)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getMentionsTimeline() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getMentionsTimeline(final Paging paging) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserTimeline(final String screenName, final Paging paging)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserTimeline(final long userId, final Paging paging)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserTimeline(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserTimeline(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserTimeline() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getUserTimeline(final Paging paging) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getHomeTimeline() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getHomeTimeline(final Paging paging) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getRetweetsOfMe() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getRetweetsOfMe(final Paging paging) throws TwitterException {
    return null;
  }

  @Override
  public Trends getPlaceTrends(final int woeid) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Location> getAvailableTrends() throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Location> getClosestTrends(final GeoLocation location)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> getRetweets(final long statusId) throws TwitterException {
    return null;
  }

  @Override
  public IDs getRetweeterIds(final long statusId, final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getRetweeterIds(final long statusId, final int count, final long cursor)
      throws TwitterException {
    return null;
  }

  @Override
  public Status showStatus(final long id) throws TwitterException {
    return null;
  }

  @Override
  public Status destroyStatus(final long statusId) throws TwitterException {
    return null;
  }

  @Override
  public Status updateStatus(final String status) throws TwitterException {
    return updateStatus(new StatusUpdate(status));
  }

  @Override
  public Status updateStatus(final StatusUpdate latestStatus) throws TwitterException {
    if (throwException) {
      throw new TwitterException("Test failure");
    }

    tweets.add(latestStatus.getStatus());
    final Status status = mock(Status.class);
    when(status.getId()).thenReturn(12345L);
    return status;
  }

  @Override
  public Status retweetStatus(final long statusId) throws TwitterException {
    return null;
  }

  @Override
  public Status unRetweetStatus(final long statusId) throws TwitterException {
    return null;
  }

  @Override
  public OEmbed getOEmbed(final OEmbedRequest req) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<Status> lookup(final long... ids) throws TwitterException {
    return null;
  }

  @Override
  public UploadedMedia uploadMedia(final File mediaFile) throws TwitterException {
    return null;
  }

  @Override
  public UploadedMedia uploadMedia(final String fileName, final InputStream media)
      throws TwitterException {
    return null;
  }

  @Override
  public UploadedMedia uploadMediaChunked(final String fileName, final InputStream media)
      throws TwitterException {
    return null;
  }

  @Override
  public AccountSettings getAccountSettings() throws TwitterException {
    return null;
  }

  @Override
  public User verifyCredentials() throws TwitterException {
    return null;
  }

  @Override
  public AccountSettings updateAccountSettings(
      final Integer trendLocationWoeid,
      final Boolean sleepTimeEnabled,
      final String startSleepTime,
      final String endSleepTime,
      final String timeZone,
      final String lang)
      throws TwitterException {
    return null;
  }

  @Override
  public AccountSettings updateAllowDmsFrom(final String allowDmsFrom) throws TwitterException {
    return null;
  }

  @Override
  public User updateProfile(
      final String name, final String url, final String location, final String description)
      throws TwitterException {
    return null;
  }

  @Override
  public User updateProfileBackgroundImage(final File image, final boolean tile)
      throws TwitterException {
    return null;
  }

  @Override
  public User updateProfileBackgroundImage(final InputStream image, final boolean tile)
      throws TwitterException {
    return null;
  }

  @Override
  public User updateProfileColors(
      final String profileBackgroundColor,
      final String profileTextColor,
      final String profileLinkColor,
      final String profileSidebarFillColor,
      final String profileSidebarBorderColor)
      throws TwitterException {
    return null;
  }

  @Override
  public User updateProfileImage(final File image) throws TwitterException {
    return null;
  }

  @Override
  public User updateProfileImage(final InputStream image) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getBlocksList() throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getBlocksList(final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getBlocksIDs() throws TwitterException {
    return null;
  }

  @Override
  public IDs getBlocksIDs(final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public User createBlock(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User createBlock(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public User destroyBlock(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User destroyBlock(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public PagableResponseList<User> getMutesList(final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public IDs getMutesIDs(final long cursor) throws TwitterException {
    return null;
  }

  @Override
  public User createMute(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User createMute(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public User destroyMute(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User destroyMute(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> lookupUsers(final long... ids) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> lookupUsers(final String... screenNames) throws TwitterException {
    return null;
  }

  @Override
  public User showUser(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public User showUser(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> searchUsers(final String query, final int page)
      throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> getContributees(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> getContributees(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> getContributors(final long userId) throws TwitterException {
    return null;
  }

  @Override
  public ResponseList<User> getContributors(final String screenName) throws TwitterException {
    return null;
  }

  @Override
  public void removeProfileBanner() throws TwitterException {
    // Not implemented
  }

  @Override
  public void updateProfileBanner(final File image) throws TwitterException {
    // Not implemented
  }

  @Override
  public void updateProfileBanner(final InputStream image) throws TwitterException {
    // Not implemented
  }

  @Override
  public OAuth2Token getOAuth2Token() throws TwitterException {
    return null;
  }

  @Override
  public void setOAuth2Token(final OAuth2Token oauth2Token) {
    // Not implemented
  }

  @Override
  public void invalidateOAuth2Token() throws TwitterException {
    // Not implemented
  }

  @Override
  public void setOAuthConsumer(final String consumerKey, final String consumerSecret) {
    // Not implemented
  }

  @Override
  public RequestToken getOAuthRequestToken() throws TwitterException {
    return null;
  }

  @Override
  public RequestToken getOAuthRequestToken(final String callbackUrl) throws TwitterException {
    return null;
  }

  @Override
  public RequestToken getOAuthRequestToken(final String callbackUrl, final String xauthAccessType)
      throws TwitterException {
    return null;
  }

  @Override
  public RequestToken getOAuthRequestToken(
      final String callbackUrl, final String xauthAccessType, final String xauthMode)
      throws TwitterException {
    return null;
  }

  @Override
  public AccessToken getOAuthAccessToken() throws TwitterException {
    return null;
  }

  @Override
  public AccessToken getOAuthAccessToken(final String oauthVerifier) throws TwitterException {
    return null;
  }

  @Override
  public AccessToken getOAuthAccessToken(final RequestToken requestToken) throws TwitterException {
    return null;
  }

  @Override
  public AccessToken getOAuthAccessToken(
      final RequestToken requestToken, final String oauthVerifier) throws TwitterException {
    return null;
  }

  @Override
  public AccessToken getOAuthAccessToken(final String screenName, final String password)
      throws TwitterException {
    return null;
  }

  @Override
  public void setOAuthAccessToken(final AccessToken accessToken) {
    // Not implemented
  }
}
