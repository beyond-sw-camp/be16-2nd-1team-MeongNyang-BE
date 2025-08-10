package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.entity.UserFollow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserFollowDetailRes {
    private String profileImage;
    private String userName;

    /**
     * @param userFollow UserFollow 엔티티
     * @param type "follower" → 나를 팔로우한 사람 / "following" → 내가 팔로우한 사람
     */
    public static UserFollowDetailRes fromEntity(UserFollow userFollow, String type){
        User targetUser;
        if ("follower".equalsIgnoreCase(type)) {
            targetUser = userFollow.getFollower();  // 나를 팔로우한 사람
        } else if ("following".equalsIgnoreCase(type)) {
            targetUser = userFollow.getFollowing(); // 내가 팔로우한 사람
        } else {
            throw new IllegalArgumentException("type은 'follower' 또는 'following'만 허용됩니다.");
        }

        return UserFollowDetailRes.builder()
                .profileImage("아직 이미지가 없어여")
                .userName(targetUser.getName())
                .build();
    }
}
