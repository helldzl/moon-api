package com.mifan.reward.domain.support;

import com.mifan.reward.domain.Codes;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel(description = "用户信息")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class RewardUsers {
    private Long id;
    private String name;
    private String avatar;
    private List<Codes> codes;
}
